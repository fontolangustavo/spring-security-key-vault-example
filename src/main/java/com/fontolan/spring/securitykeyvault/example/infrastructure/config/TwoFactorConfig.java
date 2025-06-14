package com.fontolan.spring.securitykeyvault.example.infrastructure.config;

import com.fontolan.spring.securitykeyvault.example.application.service.TwoFactorAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwoFactorConfig {
    @Bean
    public TwoFactorAuthService twoFactorAuthService() {
        return new TwoFactorAuthService();
    }
}

