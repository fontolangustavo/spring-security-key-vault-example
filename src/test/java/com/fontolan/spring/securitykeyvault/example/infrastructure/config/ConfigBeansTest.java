package com.fontolan.spring.securitykeyvault.example.infrastructure.config;

import com.fontolan.spring.securitykeyvault.example.application.service.SecretsManagerService;
import com.fontolan.spring.securitykeyvault.example.adapters.security.jwt.JwtAuthenticationFilter;
import com.fontolan.spring.securitykeyvault.example.adapters.security.oauth2.CustomOAuth2UserService;
import com.fontolan.spring.securitykeyvault.example.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ConfigBeansTest {

    @Test
    void databaseAndPasswordBeans() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(SecretsManagerService.class, SecretsManagerService::new);
        ctx.register(DatabaseConfig.class);
        ctx.register(PasswordConfig.class);
        ctx.refresh();
        assertThat(ctx.getBean(DataSource.class)).isNotNull();
        assertThat(ctx.getBean(PasswordEncoder.class)).isNotNull();
        ctx.close();
    }

    @Test
    void mailAndTwoFactorBeans() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MailConfig.class, TwoFactorConfig.class);
        assertThat(ctx.getBean(org.springframework.mail.javamail.JavaMailSender.class)).isNotNull();
        assertThat(ctx.getBean(com.fontolan.spring.securitykeyvault.example.application.service.TwoFactorAuthService.class)).isNotNull();
        ctx.close();
    }

    @Test
    void securityConfigCreatesFilterChain() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(UserService.class, () -> mock(UserService.class));
        ctx.registerBean(JwtAuthenticationFilter.class, () -> mock(JwtAuthenticationFilter.class));
        ctx.registerBean(CustomOAuth2UserService.class, () -> mock(CustomOAuth2UserService.class));
        ctx.register(SecurityConfig.class);
        ctx.refresh();
        assertThat(ctx.getBean(SecurityFilterChain.class)).isNotNull();
        ctx.close();
    }
}
