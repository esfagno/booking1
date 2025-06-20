package com.poolapp.pool.mapper;

import com.poolapp.pool.dto.PoolScheduleDTO;
import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.PoolSchedule;

public class PoolScheduleMapper {

    public static PoolScheduleDTO toDto(PoolSchedule entity) {
        return PoolScheduleDTO.builder()
                .id(entity.getId())
                .poolId(entity.getPool().getId())
                .dayOfWeek(entity.getDayOfWeek())
                .openingTime(entity.getOpeningTime())
                .closingTime(entity.getClosingTime())
                .build();
    }

    public static PoolSchedule toEntity(PoolScheduleDTO dto, Pool pool) {
        PoolSchedule schedule = new PoolSchedule();
        schedule.setId(dto.getId());
        schedule.setPool(pool);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setOpeningTime(dto.getOpeningTime());
        schedule.setClosingTime(dto.getClosingTime());
        return schedule;
    }

    public static PoolSchedule updateScheduleWith(PoolSchedule existing, PoolSchedule updated) {
        existing.setOpeningTime(updated.getOpeningTime());
        existing.setClosingTime(updated.getClosingTime());
        existing.setDayOfWeek(updated.getDayOfWeek());

        return existing;
    }
}