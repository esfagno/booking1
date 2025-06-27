package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.mapper.SessionMapper;
import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.Session;
import com.poolapp.pool.repository.PoolRepository;
import com.poolapp.pool.repository.SessionRepository;
import com.poolapp.pool.service.PoolService;
import com.poolapp.pool.service.SessionService;
import com.poolapp.pool.util.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final PoolRepository poolRepository;
    private final SessionMapper sessionMapper;
    private final PoolService poolService;

    @Override
    public Session getSessionByPoolNameAndStartTime(String poolName, LocalDateTime startTime) {
        return sessionRepository.findByPoolNameAndStartTime(poolName, startTime)
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.SESSION_NOT_FOUND + poolName + " at time: " + startTime));
    }

    @Override
    public boolean validateSessionHasAvailableSpots(SessionDTO sessionDTO) {
        if (getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime()).getCurrentCapacity() <= 0) {
            throw new IllegalStateException(ErrorMessages.NO_FREE_PLACES);
        }
        return true;
    }

    @Override
    public SessionDTO createSession(SessionDTO sessionDTO) {
        Pool pool = poolService.getPoolByName(sessionDTO.getPoolName());

        Session session = sessionMapper.toEntity(sessionDTO);
        session.setPool(pool);

        Session saved = sessionRepository.save(session);
        return sessionMapper.toDto(saved);
    }

    @Override
    public void decrementSessionCapacity(SessionDTO sessionDTO) {
        Session session = getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime());
        if (session.getCurrentCapacity() <= 0) {
            throw new IllegalStateException(ErrorMessages.NO_FREE_PLACES);
        }

        session.setCurrentCapacity(session.getCurrentCapacity() - 1);
        sessionRepository.save(session);
    }

    @Override
    public void incrementSessionCapacity(SessionDTO sessionDTO) {
        Session session = getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime());
        session.setCurrentCapacity(session.getCurrentCapacity() + 1);
        sessionRepository.save(session);
    }

    @Override
    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toDto)
                .toList();
    }
}
