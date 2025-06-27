package com.poolapp.pool.service.impl;

import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendBookingConfirmationEmail(String toEmail, SessionDTO sessionDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Подтверждение брони бассейна");
        message.setText(buildEmailBody(sessionDTO));
        javaMailSender.send(message);
    }

    private String buildEmailBody(SessionDTO sessionDTO) {
        return String.format(
                "Ваша бронь подтверждена!\nБассейн: %s\nВремя начала: %s",
                sessionDTO.getPoolName(),
                sessionDTO.getStartTime().toString()
        );
    }
}

