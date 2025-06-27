package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.mapper.SessionMapper;
import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.Session;
import com.poolapp.pool.repository.PoolRepository;
import com.poolapp.pool.repository.SessionRepository;
import com.poolapp.pool.service.PoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private PoolRepository poolRepository;

    @Mock
    private PoolService poolService;

    @Spy
    private SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Pool pool;
    private Session session;
    private SessionDTO sessionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pool = new Pool();
        pool.setId(1);
        pool.setName("Main Pool");
        pool.setMaxCapacity(20);

        session = new Session();
        session.setId(1);
        session.setPool(pool);
        session.setStartTime(LocalDateTime.of(2025, 6, 27, 10, 0));
        session.setCurrentCapacity(5);

        sessionDTO = new SessionDTO();
        sessionDTO.setPoolName("Main Pool");
        sessionDTO.setStartTime(LocalDateTime.of(2025, 6, 27, 10, 0));
    }

    @Test
    void test_createSession_shouldSaveAndReturnSessionDTO() {
        when(poolService.getPoolByName("Main Pool")).thenReturn(pool);
        when(sessionRepository.save(any())).thenReturn(session);

        SessionDTO result = sessionService.createSession(sessionDTO);

        assertNotNull(result);
        assertEquals("Main Pool", result.getPoolName());
        verify(poolService, times(1)).getPoolByName("Main Pool");
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    void test_decrementSessionCapacity_shouldDecreaseCapacity() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        sessionService.decrementSessionCapacity(sessionDTO);

        assertEquals(4, session.getCurrentCapacity());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void test_decrementSessionCapacity_shouldThrowIfNoFreePlaces() {
        session.setCurrentCapacity(0);
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> sessionService.decrementSessionCapacity(sessionDTO));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void test_decrementSessionCapacity_shouldThrowIfSessionNotFound() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> sessionService.decrementSessionCapacity(sessionDTO));
    }

    @Test
    void test_incrementSessionCapacity_shouldIncreaseCapacity() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        sessionService.incrementSessionCapacity(sessionDTO);

        assertEquals(6, session.getCurrentCapacity());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void test_incrementSessionCapacity_shouldThrowIfSessionNotFound() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> sessionService.incrementSessionCapacity(sessionDTO));
    }

    @Test
    void test_getAllSessions_shouldReturnSessionDTOList() {
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<SessionDTO> result = sessionService.getAllSessions();

        assertEquals(1, result.size());
        assertEquals("Main Pool", result.get(0).getPoolName());
    }

    @Test
    void test_getSessionByPoolNameAndStartTime_shouldReturnSession() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        Session result = sessionService.getSessionByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime());

        assertNotNull(result);
        assertEquals(session, result);
    }

    @Test
    void test_getSessionByPoolNameAndStartTime_shouldThrowIfNotFound() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> sessionService.getSessionByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()));
    }

    @Test
    void test_validateSessionHasAvailableSpots_shouldReturnTrueIfAvailable() {
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        boolean result = sessionService.validateSessionHasAvailableSpots(sessionDTO);

        assertTrue(result);
    }

    @Test
    void test_validateSessionHasAvailableSpots_shouldThrowIfNoSpots() {
        session.setCurrentCapacity(0);
        when(sessionRepository.findByPoolNameAndStartTime("Main Pool", sessionDTO.getStartTime()))
                .thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> sessionService.validateSessionHasAvailableSpots(sessionDTO));
    }
}
