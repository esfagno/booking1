package com.poolapp.pool.service;

import com.poolapp.pool.dto.BookingDTO;
import com.poolapp.pool.dto.SessionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDTO createBooking(BookingDTO bookingDTO, SessionDTO sessionDTO);

    BookingDTO updateBooking(BookingDTO bookingDTO, SessionDTO oldSession, SessionDTO newSession);

    boolean hasUserBooked(String userEmail, LocalDateTime sessionStartTime);

    void expirePastBookings(LocalDateTime now);

    void cancelAllUserBookings(String userEmail);

    void cancelBooking(BookingDTO bookingDTO, SessionDTO sessionDTO);

    void deleteBooking(BookingDTO bookingDTO, SessionDTO sessionDTO);

    List<BookingDTO> findBookingsByFilter(BookingDTO bookingDTO, SessionDTO sessionDTO);

    void deleteBookingsBySession(SessionDTO sessionDTO);

    long countBookingsBySession(SessionDTO sessionDTO);
}
