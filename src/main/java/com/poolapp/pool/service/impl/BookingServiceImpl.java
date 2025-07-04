package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.BookingDTO;
import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.exception.BookingAlreadyActiveException;
import com.poolapp.pool.exception.BookingStatusNotActiveException;
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
import com.poolapp.pool.repository.specification.builder.BookingSpecificationBuilder;
import com.poolapp.pool.service.BookingService;
import com.poolapp.pool.service.MailService;
import com.poolapp.pool.service.SessionService;
import com.poolapp.pool.service.UserService;
import com.poolapp.pool.util.CapacityOperation;
import com.poolapp.pool.util.ChangeSessionCapacityRequest;
import com.poolapp.pool.util.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    private final BookingSpecificationBuilder bookingSpecificationBuilder;


    @Transactional
    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {

//I will add the subscription implementation later and add and I’ll add the subscription logic here

        BookingId bookingId = buildBookingId(bookingDTO);
        if (userService.hasActiveBooking(bookingDTO.getUserEmail(), LocalDateTime.now())) {
            throw new BookingAlreadyActiveException(ErrorMessages.ALREADY_ACTIVE);
        }
        sessionService.validateSessionHasAvailableSpots(bookingDTO.getSessionDTO());

        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking.setId(bookingId);

        Booking saved = bookingRepository.save(booking);
        ChangeSessionCapacityRequest request = new ChangeSessionCapacityRequest();
        request.setSessionDTO(bookingDTO.getSessionDTO());
        request.setOperation(CapacityOperation.DECREASE);
        sessionService.changeSessionCapacity(request);
        mailService.sendBookingConfirmationEmail(bookingDTO.getUserEmail(), bookingDTO.getSessionDTO());

        return bookingMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteBooking(BookingDTO bookingDTO) {
        bookingRepository.deleteById(buildBookingId(bookingDTO));
        ChangeSessionCapacityRequest request = new ChangeSessionCapacityRequest();
        request.setSessionDTO(bookingDTO.getSessionDTO());
        request.setOperation(CapacityOperation.INCREASE);
        sessionService.changeSessionCapacity(request);
    }

    @Transactional
    @Override
    public void cancelBooking(BookingDTO bookingDTO) {
        Booking booking = findBookingByDTO(bookingDTO);
        switch (booking.getStatus()) {
            case ACTIVE -> booking.setStatus(BookingStatus.CANCELLED);
            default -> throw new BookingStatusNotActiveException(ErrorMessages.WRONG_STATUS + booking.getStatus());
        }
        ChangeSessionCapacityRequest request = new ChangeSessionCapacityRequest();
        request.setSessionDTO(bookingDTO.getSessionDTO());
        request.setOperation(CapacityOperation.INCREASE);
        sessionService.changeSessionCapacity(request);
    }

    @Override
    public List<BookingDTO> findBookingsByFilter(BookingDTO filterDTO) {
        BookingId bookingId = buildBookingId(filterDTO);
        Booking filter = bookingMapper.toEntity(filterDTO);
        filter.setId(bookingId);
        Specification<Booking> spec = bookingSpecificationBuilder.buildSpecification(filter);
        List<Booking> bookings = bookingRepository.findAll(spec);
        return bookingMapper.toDtoList(bookings);
    }

    @Override
    public BookingDTO updateBooking(BookingDTO bookingDTO, BookingDTO newBookingDTO) {
        Booking booking = findBookingByDTO(bookingDTO);
        bookingMapper.updateBookingFromDto(booking, newBookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
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
    public void deleteBookingsBySession(SessionDTO sessionDTO) {
        Session session = sessionService.getSessionByPoolNameAndStartTime(sessionDTO.getPoolDTO().getName(), sessionDTO.getStartTime())
                .orElseThrow(() -> new ModelNotFoundException(String.format(ErrorMessages.SESSION_NOT_FOUND, sessionDTO.getPoolDTO().getName(), sessionDTO.getStartTime())));
        bookingRepository.deleteAllBySessionId(session.getId());
    }

    @Override
    public Long countBookingsBySession(SessionDTO sessionDTO) {
        Session session = sessionService.getSessionByPoolNameAndStartTime(sessionDTO.getPoolDTO().getName(), sessionDTO.getStartTime())
                .orElseThrow(() -> new ModelNotFoundException(String.format(ErrorMessages.SESSION_NOT_FOUND, sessionDTO.getPoolDTO().getName(), sessionDTO.getStartTime())));
        return bookingRepository.countBySessionId(session.getId());
    }

    private Booking findBookingByDTO(BookingDTO bookingDTO) {
        return bookingRepository.findById(buildBookingId(bookingDTO))
                .orElseThrow(() -> new ModelNotFoundException(ErrorMessages.BOOKING_NOT_FOUND));
    }

    private BookingId buildBookingId(BookingDTO bookingDTO) {
        User user = userService.findUserByEmail(bookingDTO.getUserEmail())
                .orElseThrow(() -> new ModelNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, bookingDTO.getUserEmail())));

        Session session = sessionService.getSessionByPoolNameAndStartTime(bookingDTO.getSessionDTO().getPoolDTO().getName(), bookingDTO.getSessionDTO().getStartTime())
                .orElseThrow(() -> new ModelNotFoundException(String.format(ErrorMessages.SESSION_NOT_FOUND, bookingDTO.getSessionDTO().getPoolDTO().getName(), bookingDTO.getSessionDTO().getStartTime())));
        return new BookingId(user.getId(), session.getId());
    }


}
