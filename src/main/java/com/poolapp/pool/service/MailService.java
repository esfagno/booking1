package com.poolapp.pool.service;

import com.poolapp.pool.dto.SessionDTO;

public interface MailService {
    void sendBookingConfirmationEmail(String toEmail, SessionDTO sessionDTO);
}

