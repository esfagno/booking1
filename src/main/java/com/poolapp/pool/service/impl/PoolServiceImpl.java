package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.PoolDTO;
import com.poolapp.pool.dto.PoolScheduleDTO;
import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.mapper.PoolMapper;
import com.poolapp.pool.mapper.PoolScheduleMapper;
import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.PoolSchedule;
import com.poolapp.pool.repository.PoolRepository;
import com.poolapp.pool.repository.PoolScheduleRepository;
import com.poolapp.pool.service.PoolService;
import com.poolapp.pool.util.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolServiceImpl implements PoolService {

    private final PoolRepository poolRepository;
    private final PoolScheduleRepository scheduleRepository;

    @Transactional
    @Override
    public PoolDTO createPool(PoolDTO dto) {
        Pool pool = PoolMapper.toEntity(dto);
        Pool saved = poolRepository.save(pool);
        return PoolMapper.toDto(saved);

    }

    @Override
    public Pool getPoolById(Integer id) {
        return poolRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.POOL_NOT_FOUND + id));
    }

    @Override
    public List<PoolDTO> searchPools(String name, String address, String description, Integer maxCapacity, Integer sessionDuration) {

        List<Pool> pools = poolRepository.findPoolByFilter(name, address, description, maxCapacity, sessionDuration);
        return pools.stream()
                .map(PoolMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public PoolDTO updatePool(Integer id, PoolDTO updatedPool) {
        Pool pool = getPoolById(id);
        PoolMapper.updatePoolFromDto(pool, updatedPool);
        Pool saved = poolRepository.save(pool);
        return PoolMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deletePool(Integer id) {
        if (!poolRepository.existsById(id)) {
            throw new ModelNotFoundException(ErrorMessages.POOL_NOT_FOUND + id);
        }
        poolRepository.deleteById(id);
    }

    @Transactional
    @Override
    public PoolDTO updateCapacity(Integer poolId, Integer newCapacity) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.POOL_NOT_FOUND + poolId));
        pool.setMaxCapacity(newCapacity);
        Pool saved = poolRepository.save(pool);
        return PoolMapper.toDto(saved);
    }

    @Transactional
    @Override
    public PoolScheduleDTO createOrUpdateSchedule(Integer poolId, PoolScheduleDTO dto) {
        Pool pool = getPoolById(poolId);
        PoolSchedule schedule = PoolScheduleMapper.toEntity(dto, pool);

        PoolSchedule saved = scheduleRepository.findByPoolIdAndDayOfWeek(poolId, dto.getDayOfWeek())
                .map(existing -> {
                    PoolScheduleMapper.updateScheduleWith(existing, schedule);
                    return scheduleRepository.save(existing);
                })
                .orElseGet(() -> scheduleRepository.save(schedule));

        return PoolScheduleMapper.toDto(saved);
    }

    @Transactional
    @Override
    public PoolScheduleDTO updateSchedule(Integer scheduleId, PoolScheduleDTO dto) {
        PoolSchedule existing = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.SCHEDULE_NOT_FOUND + scheduleId));

        PoolSchedule incoming = PoolScheduleMapper.toEntity(dto, existing.getPool());
        PoolScheduleMapper.updateScheduleWith(existing, incoming);

        PoolSchedule saved = scheduleRepository.save(existing);
        return PoolScheduleMapper.toDto(saved);
    }

    @Override
    public void deleteScheduleByDay(Integer poolId, Short dayOfWeek) {
        if (!scheduleRepository.existsByPoolIdAndDayOfWeek(poolId, dayOfWeek)) {
            throw new ModelNotFoundException(
                    ErrorMessages.SCHEDULE_NOT_FOUND
                            + " for poolId=" + poolId + " and dayOfWeek=" + dayOfWeek
            );
        }
        scheduleRepository.deleteByPoolIdAndDayOfWeek(poolId, dayOfWeek);
    }

    @Override
    public List<PoolScheduleDTO> getSchedulesForPool(Integer poolId) {

        List<PoolSchedule> schedules = scheduleRepository.findByPoolId(poolId);
        return schedules.stream()
                .map(PoolScheduleMapper::toDto)
                .toList();
    }


    @Override
    public List<PoolDTO> getPoolsByDayOfWeek(Short dayOfWeek) {
        List<Pool> pools = scheduleRepository.findPoolsByDayOfWeek(dayOfWeek);
        return pools.stream()
                .map(PoolMapper::toDto)
                .toList();
    }

}
