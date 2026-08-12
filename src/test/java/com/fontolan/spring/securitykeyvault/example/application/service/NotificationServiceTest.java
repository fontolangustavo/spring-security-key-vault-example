package com.fontolan.spring.securitykeyvault.example.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {
    private JavaMailSender sender;
    private NotificationService service;

    @BeforeEach
    void setup() {
        sender = mock(JavaMailSender.class);
        service = new NotificationService(sender);
    }

    @Test
    void sendTokenMail() {
        service.sendToken("to", "123");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertThat(msg.getSubject()).isEqualTo("Your security token");
        assertThat(msg.getText()).contains("123");
    }

    @Test
    void sendSuspiciousLoginMail() {
        service.sendSuspiciousLogin("to", "info");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertThat(msg.getSubject()).isEqualTo("New device login detected");
        assertThat(msg.getText()).contains("info");
    }
}
