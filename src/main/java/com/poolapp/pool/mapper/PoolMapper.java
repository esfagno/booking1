package com.poolapp.pool.mapper;

import com.poolapp.pool.dto.PoolDTO;
import com.poolapp.pool.model.Pool;

public class PoolMapper {

    public static void updatePoolFromDto(Pool pool, PoolDTO dto) {
        if (dto.getName() != null) {
            pool.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            pool.setAddress(dto.getAddress());
        }
        if (dto.getDescription() != null) {
            pool.setDescription(dto.getDescription());
        }
        if (dto.getMaxCapacity() != null) {
            pool.setMaxCapacity(dto.getMaxCapacity());
        }
        if (dto.getSessionDurationMinutes() != null) {
            pool.setSessionDurationMinutes(dto.getSessionDurationMinutes());
        }
    }

    public static Pool toEntity(PoolDTO dto) {
        return Pool.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .maxCapacity(dto.getMaxCapacity())
                .sessionDurationMinutes(dto.getSessionDurationMinutes())
                .build();
    }

    public static Pool updatePoolWith(Pool existingPool, Pool updatedPool) {
        return existingPool.toBuilder()
                .name(updatedPool.getName())
                .address(updatedPool.getAddress())
                .description(updatedPool.getDescription())
                .maxCapacity(updatedPool.getMaxCapacity())
                .sessionDurationMinutes(updatedPool.getSessionDurationMinutes())
                .build();
    }

    public static PoolDTO toDto(Pool pool) {
        if (pool == null) {
            return null;
        }
        return PoolDTO.builder()
                .id(pool.getId())
                .name(pool.getName())
                .address(pool.getAddress())
                .description(pool.getDescription())
                .maxCapacity(pool.getMaxCapacity())
                .sessionDurationMinutes(pool.getSessionDurationMinutes())
                .build();
    }


}
