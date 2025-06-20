package com.poolapp.pool.repository;

import com.poolapp.pool.model.Pool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PoolRepository extends JpaRepository<Pool, Integer> {
    @Query("""
                SELECT p FROM Pool p WHERE
                (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%')))
                AND (:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%')))
                AND (:maxCapacity IS NULL OR p.maxCapacity = :maxCapacity)
                AND (:sessionDuration IS NULL OR p.sessionDurationMinutes = :sessionDuration)
            """)
    List<Pool> findPoolByFilter(@Param("name") String name,
                                @Param("address") String address,
                                @Param("description") String description,
                                @Param("maxCapacity") Integer maxCapacity,
                                @Param("sessionDuration") Integer sessionDuration);

}
