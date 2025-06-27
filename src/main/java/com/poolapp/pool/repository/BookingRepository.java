package com.poolapp.pool.repository;

import com.poolapp.pool.dto.BookingDTO;
import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.model.Booking;
import com.poolapp.pool.model.BookingId;
import com.poolapp.pool.model.enums.BookingStatus;
import com.poolapp.pool.repository.custom.BookingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, BookingId>, BookingRepositoryCustom {

    long countBySessionId(Integer sessionId);

    void deleteAllBySessionId(Integer sessionId);

    List<Booking> findByUserEmail(String email);

    boolean existsBySessionId(Integer sessionId);

    boolean existsByUserIdAndSessionStartTimeAfter(Integer userId, LocalDateTime startTime);

    Optional<Booking> findByUserEmailAndSessionPoolNameAndBookingTime(String userEmail, String poolName, LocalDateTime bookingTime);

    List<Booking> findByUser_EmailAndSession_StartTime(String email, LocalDateTime startTime);

    List<Booking> findBySession_StartTimeBeforeAndStatus(LocalDateTime time, BookingStatus status);

    List<Booking> findByUser_EmailAndStatus(String email, BookingStatus status);

}
