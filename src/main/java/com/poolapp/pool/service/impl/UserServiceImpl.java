package com.poolapp.pool.service.impl;

import com.poolapp.pool.exception.ModelNotFoundException;
import com.poolapp.pool.model.User;
import com.poolapp.pool.repository.BookingRepository;
import com.poolapp.pool.repository.UserRepository;
import com.poolapp.pool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    //Spring Security??

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ModelNotFoundException("User not found: " + email));
    }

    public boolean hasActiveBooking(String email, LocalDateTime currentTime) {
        return bookingRepository.existsByUserIdAndSessionStartTimeAfter(findUserByEmail(email).getId(), currentTime);
    }
}

