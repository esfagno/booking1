package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.PoolDTO;
import com.poolapp.pool.dto.PoolScheduleDTO;
import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.mapper.PoolScheduleMapper;
import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.PoolSchedule;
import com.poolapp.pool.repository.PoolRepository;
import com.poolapp.pool.repository.PoolScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PoolServiceImplTest {

    @Mock
    private PoolRepository poolRepository;

    @Mock
    private PoolScheduleRepository scheduleRepository;

    @InjectMocks
    private PoolServiceImpl poolService;

    private Pool pool;
    private PoolDTO poolDTO;
    private PoolSchedule schedule;
    private PoolScheduleDTO scheduleDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pool = new Pool();
        pool.setId(1);
        pool.setName("Main Pool");
        pool.setMaxCapacity(20);

        poolDTO = new PoolDTO();
        poolDTO.setName("Main Pool");
        poolDTO.setMaxCapacity(20);

        schedule = new PoolSchedule();
        schedule.setId(1);
        schedule.setPool(pool);
        schedule.setDayOfWeek((short) 1);
        schedule.setOpeningTime(LocalTime.of(8, 0));
        schedule.setClosingTime(LocalTime.of(20, 0));

        scheduleDTO = new PoolScheduleDTO();
        scheduleDTO.setDayOfWeek((short) 1);
        scheduleDTO.setOpeningTime(LocalTime.of(8, 0));
        scheduleDTO.setClosingTime(LocalTime.of(20, 0));
    }

    @Test
    void test_createPool_shouldReturnSavedPoolDTO() {
        when(poolRepository.save(any())).thenReturn(pool);

        PoolDTO result = poolService.createPool(poolDTO);

        assertNotNull(result);
        assertEquals(pool.getName(), result.getName());
        verify(poolRepository).save(any());
    }

    @Test
    void test_getPoolById_shouldReturnPoolIfFound() {
        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        Pool result = poolService.getPoolById(1);
        assertEquals(pool, result);
    }

    @Test
    void test_getPoolById_shouldThrowIfNotFound() {
        when(poolRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> poolService.getPoolById(1));
    }

    @Test
    void test_searchPools_shouldFilterByName() {
        when(poolRepository.findPoolByFilter("main", null, null, null, null))
                .thenReturn(List.of(pool));

        List<PoolDTO> result = poolService.searchPools("main", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Main Pool", result.get(0).getName());
    }

    @Test
    void test_searchPools_shouldFilterByMultipleFields() {
        when(poolRepository.findPoolByFilter("main", "Some Address", null, 20, 60))
                .thenReturn(List.of(pool));

        List<PoolDTO> result = poolService.searchPools("main", "Some Address", null, 20, 60);

        assertEquals(1, result.size());
        assertEquals("Main Pool", result.get(0).getName());
    }

    @Test
    void test_searchPools_shouldReturnAllIfNoFilter() {
        when(poolRepository.findPoolByFilter(null, null, null, null, null))
                .thenReturn(List.of(pool));

        List<PoolDTO> result = poolService.searchPools(null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Main Pool", result.get(0).getName());
    }


    @Test
    void test_updatePool_shouldMergeAndSave() {
        PoolDTO updated = new PoolDTO();
        updated.setName("Updated Pool");

        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(poolRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PoolDTO result = poolService.updatePool(1, updated);
        assertEquals("Updated Pool", result.getName());
    }

    @Test
    void test_deletePool_shouldDeleteIfExists() {
        when(poolRepository.existsById(1)).thenReturn(true);
        poolService.deletePool(1);
        verify(poolRepository).deleteById(1);
    }

    @Test
    void test_deletePool_shouldThrowIfNotExists() {
        when(poolRepository.existsById(1)).thenReturn(false);
        assertThrows(ModelNotFoundException.class, () -> poolService.deletePool(1));
    }

    @Test
    void test_updateCapacity_shouldSetAndSaveNewCapacity() {
        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(poolRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PoolDTO result = poolService.updateCapacity(1, 30);
        assertNotNull(result);
        assertEquals(30, result.getMaxCapacity());
        verify(poolRepository).save(any());
    }

    @Test
    void test_createOrUpdateSchedule_shouldCreateNewIfNotExists() {
        PoolScheduleDTO dto = new PoolScheduleDTO();
        dto.setDayOfWeek((short) 1);
        dto.setOpeningTime(LocalTime.of(8, 0));
        dto.setClosingTime(LocalTime.of(20, 0));

        PoolSchedule scheduleEntity = PoolScheduleMapper.toEntity(dto, pool);

        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(scheduleRepository.findByPoolIdAndDayOfWeek(1, (short) 1)).thenReturn(Optional.empty());
        when(scheduleRepository.save(any())).thenReturn(scheduleEntity);

        PoolScheduleDTO result = poolService.createOrUpdateSchedule(1, dto);
        assertEquals(dto.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(dto.getOpeningTime(), result.getOpeningTime());
        assertEquals(dto.getClosingTime(), result.getClosingTime());
    }

    @Test
    void test_createOrUpdateSchedule_shouldUpdateIfExists() {
        PoolSchedule existing = new PoolSchedule();
        existing.setId(2);
        existing.setPool(pool);
        existing.setDayOfWeek((short) 1);
        existing.setOpeningTime(LocalTime.of(7, 0));
        existing.setClosingTime(LocalTime.of(21, 0));

        PoolScheduleDTO dto = PoolScheduleMapper.toDto(schedule);

        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(scheduleRepository.findByPoolIdAndDayOfWeek(1, (short) 1)).thenReturn(Optional.of(existing));
        when(scheduleRepository.save(any())).thenReturn(schedule);

        PoolScheduleDTO result = poolService.createOrUpdateSchedule(1, dto);
        assertEquals(dto.getDayOfWeek(), result.getDayOfWeek());
    }

    @Test
    void test_updateSchedule_shouldUpdateIfExists() {
        PoolScheduleDTO updatedDto = new PoolScheduleDTO();
        updatedDto.setOpeningTime(LocalTime.of(9, 0));
        updatedDto.setClosingTime(LocalTime.of(18, 0));
        updatedDto.setDayOfWeek(schedule.getDayOfWeek());

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PoolScheduleDTO result = poolService.updateSchedule(1, updatedDto);
        assertEquals(LocalTime.of(9, 0), result.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), result.getClosingTime());
    }

    @Test
    void test_updateSchedule_shouldThrowIfNotFound() {
        PoolScheduleDTO dto = new PoolScheduleDTO();
        dto.setDayOfWeek((short) 1);
        dto.setOpeningTime(LocalTime.of(9, 0));
        dto.setClosingTime(LocalTime.of(18, 0));

        when(scheduleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> poolService.updateSchedule(1, dto));
    }

    @Test
    void test_deleteScheduleByDay_shouldDeleteByDayOfWeek() {
        when(scheduleRepository.existsByPoolIdAndDayOfWeek(1, (short) 1))
                .thenReturn(true);
        poolService.deleteScheduleByDay(1, (short) 1);
        verify(scheduleRepository).deleteByPoolIdAndDayOfWeek(1, (short) 1);
    }

    @Test
    void test_getSchedulesForPool_shouldReturnList() {
        when(scheduleRepository.findByPoolId(1)).thenReturn(List.of(schedule));
        List<PoolScheduleDTO> result = poolService.getSchedulesForPool(1);
        assertEquals(1, result.size());
        assertEquals(schedule.getOpeningTime(), result.get(0).getOpeningTime());
    }

    @Test
    void test_getPoolsByDayOfWeek_shouldReturnPools() {
        when(scheduleRepository.findPoolsByDayOfWeek((short) 1)).thenReturn(List.of(pool));
        List<PoolDTO> result = poolService.getPoolsByDayOfWeek((short) 1);
        assertEquals(1, result.size());
        assertEquals(pool.getName(), result.get(0).getName());
    }

    @Test
    void test_deleteScheduleByDay_shouldThrowIfNotExists() {
        when(scheduleRepository.existsByPoolIdAndDayOfWeek(1, (short) 1)).thenReturn(false);
        assertThrows(ModelNotFoundException.class,
                () -> poolService.deleteScheduleByDay(1, (short) 1));
    }

    @Test
    void test_createOrUpdateSchedule_shouldThrowIfPoolNotFound() {
        when(poolRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class,
                () -> poolService.createOrUpdateSchedule(1, scheduleDTO));
    }

    @Test
    void test_updatePool_shouldThrowIfNotFound() {
        when(poolRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class,
                () -> poolService.updatePool(1, poolDTO));
    }


}
