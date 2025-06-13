package com.fontolan.spring.securitykeyvault.example.configs;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwoFactorConfig {
    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        return new GoogleAuthenticator();
    }
}
