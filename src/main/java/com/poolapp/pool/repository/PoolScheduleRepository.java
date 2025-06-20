package com.poolapp.pool.repository;

import com.poolapp.pool.model.Pool;
import com.poolapp.pool.model.PoolSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PoolScheduleRepository extends JpaRepository<PoolSchedule, Integer> {
    Optional<PoolSchedule> findByPoolIdAndDayOfWeek(Integer poolId, Short dayOfWeek);

    List<PoolSchedule> findAllByDayOfWeek(Short dayOfWeek);

    List<PoolSchedule> findByPoolId(Integer poolId);

    @Query("SELECT DISTINCT ps.pool FROM PoolSchedule ps WHERE ps.dayOfWeek = :dayOfWeek")
    List<Pool> findPoolsByDayOfWeek(@Param("dayOfWeek") Short dayOfWeek);

    void deleteByPoolIdAndDayOfWeek(Integer poolId, Short dayOfWeek);

    boolean existsByPoolIdAndDayOfWeek(Integer poolId, Short dayOfWeek);
}

