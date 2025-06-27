package com.poolapp.pool.repository.impl;

import com.poolapp.pool.model.Pool;
import com.poolapp.pool.repository.base.criteria.PoolCriteria;
import com.poolapp.pool.repository.custom.PoolRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PoolRepositoryImpl implements PoolRepositoryCustom {

    private final PoolCriteria poolCriteria;

    @Override
    public List<Pool> searchPoolByFilter(String name, String address, String description,
                                         Integer maxCapacity, Integer sessionDuration) {
        return poolCriteria.searchPoolByFilter(name, address, description, maxCapacity, sessionDuration);
    }
}
