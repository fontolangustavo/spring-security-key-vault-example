package com.fontolan.spring.securitykeyvault.example.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendToken(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your security token");
        message.setText("Token: " + token);
        mailSender.send(message);
    }

    public void sendSuspiciousLogin(String to, String deviceInfo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New device login detected");
        message.setText("A login from an unrecognized device was detected: " + deviceInfo);
        mailSender.send(message);
    }
}
