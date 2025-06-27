package com.poolapp.pool.repository.impl;

import com.poolapp.pool.model.Booking;
import com.poolapp.pool.model.enums.BookingStatus;
import com.poolapp.pool.repository.base.criteria.BookingCriteria;
import com.poolapp.pool.repository.custom.BookingRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepositoryCustom {

    private final BookingCriteria bookingCriteria;

    public List<Booking> findBookingsByFilter(String userEmail, BookingStatus status, String poolName, LocalDateTime startTime) {
        return bookingCriteria.findBookingsByFilter(userEmail, status, poolName, startTime);
    }

}
