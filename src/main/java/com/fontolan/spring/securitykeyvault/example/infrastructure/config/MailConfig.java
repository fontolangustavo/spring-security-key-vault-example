package com.fontolan.spring.securitykeyvault.example.infrastructure.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                System.out.println("[MOCK EMAIL] To: " + simpleMessage.getTo()[0]);
                System.out.println("[MOCK EMAIL] Subject: " + simpleMessage.getSubject());
                System.out.println("[MOCK EMAIL] Text: " + simpleMessage.getText());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }

            // Os outros métodos da interface podem ficar vazios
            @Override
            public MimeMessage createMimeMessage() { return null; }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) { return null; }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException { }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException { }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException { }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException { }
        };
    }
}
