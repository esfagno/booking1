package com.poolapp.pool.repository.custom;

import com.poolapp.pool.model.Pool;

import java.util.List;

public interface PoolRepositoryCustom {
    List<Pool> searchPoolByFilter(String name, String address, String description, Integer maxCapacity, Integer sessionDuration);
}