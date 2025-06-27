package com.poolapp.pool.repository;

import com.poolapp.pool.model.Pool;
import com.poolapp.pool.repository.custom.PoolRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PoolRepository extends JpaRepository<Pool, Integer>, PoolRepositoryCustom {

    Optional<Pool> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);

}
