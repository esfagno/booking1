package com.poolapp.pool.service;

import com.poolapp.pool.dto.PoolDTO;
import com.poolapp.pool.dto.PoolScheduleDTO;
import com.poolapp.pool.model.Pool;

import java.util.List;

public interface PoolService {
    PoolDTO createPool(PoolDTO pool);

    Pool getPoolById(Integer id);

    List<PoolDTO> searchPools(String name, String address, String description, Integer maxCapacity, Integer sessionDuration);

    PoolDTO updatePool(Integer id, PoolDTO updatedPool);

    void deletePool(Integer id);

    PoolDTO updateCapacity(Integer poolId, Integer newCapacity);

    PoolScheduleDTO createOrUpdateSchedule(Integer poolId, PoolScheduleDTO dto);

    PoolScheduleDTO updateSchedule(Integer scheduleId, PoolScheduleDTO dto);

    void deleteScheduleByDay(Integer poolId, Short dayOfWeek);

    List<PoolScheduleDTO> getSchedulesForPool(Integer poolId);

    List<PoolDTO> getPoolsByDayOfWeek(Short dayOfWeek);
}