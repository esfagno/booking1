package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.BookingDTO;
import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.mapper.BookingMapper;
import com.poolapp.pool.model.Booking;
import com.poolapp.pool.model.BookingId;
import com.poolapp.pool.model.Session;
import com.poolapp.pool.model.User;
import com.poolapp.pool.model.enums.BookingStatus;
import com.poolapp.pool.repository.BookingRepository;
import com.poolapp.pool.repository.PoolRepository;
import com.poolapp.pool.repository.SessionRepository;
import com.poolapp.pool.repository.UserRepository;
import com.poolapp.pool.service.BookingService;
import com.poolapp.pool.service.MailService;
import com.poolapp.pool.service.SessionService;
import com.poolapp.pool.service.UserService;
import com.poolapp.pool.util.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BookingMapper bookingMapper;
    private final PoolRepository poolRepository;
    private final UserService userService;
    private final SessionService sessionService;
    private final MailService mailService;


    public Booking findBooking(BookingDTO bookingDTO, SessionDTO sessionDTO) {
        return bookingRepository.findById(buildBookingId(bookingDTO, sessionDTO))
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.BOOKING_NOT_FOUND));
    }

    public BookingId buildBookingId(BookingDTO bookingDTO, SessionDTO sessionDTO) {
        User user = userService.findUserByEmail(bookingDTO.getUserEmail());
        Session session = sessionService.getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime());
        return new BookingId(user.getId(), session.getId());
    }

    @Transactional
    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO, SessionDTO sessionDTO) {

//I will add the subscription implementation later and add and I’ll add the subscription logic here

        BookingId bookingId = buildBookingId(bookingDTO, sessionDTO);
        userService.hasActiveBooking(bookingDTO.getUserEmail(), LocalDateTime.now());
        sessionService.validateSessionHasAvailableSpots(sessionDTO);

        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking.setId(bookingId);

        Booking saved = bookingRepository.save(booking);
        sessionService.decrementSessionCapacity(sessionDTO);
        mailService.sendBookingConfirmationEmail(bookingDTO.getUserEmail(), sessionDTO);

        return bookingMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteBooking(BookingDTO bookingDTO, SessionDTO sessionDTO) {
        bookingRepository.deleteById(buildBookingId(bookingDTO, sessionDTO));
        sessionService.incrementSessionCapacity(sessionDTO);
    }

    @Transactional
    @Override
    public void cancelBooking(BookingDTO bookingDTO, SessionDTO sessionDTO) {
        if (findBooking(bookingDTO, sessionDTO).getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException(ErrorMessages.WRONG_STATUS + findBooking(bookingDTO, sessionDTO).getStatus());
        }
        findBooking(bookingDTO, sessionDTO).setStatus(BookingStatus.CANCELLED);
        sessionService.incrementSessionCapacity(sessionDTO);
    }

    @Override
    public List<BookingDTO> findBookingsByFilter(BookingDTO bookingDTO, SessionDTO sessionDTO) {
        List<Booking> bookings = bookingRepository.findBookingsByFilter(bookingDTO.getUserEmail(), findBooking(bookingDTO, sessionDTO).getStatus(), sessionDTO.getPoolName(), sessionDTO.getStartTime());
        return bookingMapper.toDtoList(bookings);
    }

    @Override
    @Transactional
    public BookingDTO updateBooking(BookingDTO bookingDTO, SessionDTO oldSession, SessionDTO newSession) {
        deleteBooking(bookingDTO, oldSession);
        return createBooking(bookingDTO, newSession);
    }

    @Override
    public boolean hasUserBooked(String userEmail, LocalDateTime sessionStartTime) {
        return !bookingRepository.findByUser_EmailAndSession_StartTime(userEmail, sessionStartTime).isEmpty();
    }

    @Override
    @Transactional
    public void expirePastBookings(LocalDateTime now) {
        List<Booking> expired = bookingRepository.findBySession_StartTimeBeforeAndStatus(now, BookingStatus.ACTIVE);
        expired.forEach(b -> b.setStatus(BookingStatus.COMPLETED));
        bookingRepository.saveAll(expired);
    }

    @Override
    @Transactional
    public void cancelAllUserBookings(String userEmail) {
        List<Booking> active = bookingRepository.findByUser_EmailAndStatus(userEmail, BookingStatus.ACTIVE);
        active.forEach(b -> b.setStatus(BookingStatus.CANCELLED));
        bookingRepository.saveAll(active);
    }

    @Override
    public void deleteBookingsBySession(SessionDTO sessionDTO) {
        Session session = sessionService.getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime());
        bookingRepository.deleteAllBySessionId(session.getId());

    }

    @Override
    public long countBookingsBySession(SessionDTO sessionDTO) {
        Session session = sessionService.getSessionByPoolNameAndStartTime(sessionDTO.getPoolName(), sessionDTO.getStartTime());
        return bookingRepository.countBySessionId(session.getId());
    }

}
