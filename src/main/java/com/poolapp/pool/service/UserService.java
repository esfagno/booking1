package com.poolapp.pool.service;

import com.poolapp.pool.model.User;

import java.time.LocalDateTime;

public interface UserService {
    User findUserByEmail(String email);
    boolean hasActiveBooking(String email, LocalDateTime currentTime);
}

