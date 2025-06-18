package com.fontolan.spring.securitykeyvault.example.application.service;

import com.fontolan.spring.securitykeyvault.example.domain.model.Role;
import com.fontolan.spring.securitykeyvault.example.domain.model.User;
import com.fontolan.spring.securitykeyvault.example.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository repository;
    private PasswordEncoder encoder;
    private NotificationService notification;
    private TwoFactorAuthService twoFactor;
    private UserService service;

    @BeforeEach
    void setup() {
        repository = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        notification = mock(NotificationService.class);
        twoFactor = mock(TwoFactorAuthService.class);
        service = new UserService(repository, encoder, notification, twoFactor);
    }

    @Test
    void saveEncodesPasswordAndGeneratesSecret() {
        User user = new User();
        user.setUsername("u");
        user.setPassword("p");
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setTwoFactorEnabled(true);
        when(encoder.encode("p")).thenReturn("enc");
        when(twoFactor.generateSecret()).thenReturn("sec");

        service.save(user);

        assertThat(user.getPassword()).isEqualTo("enc");
        assertThat(user.getTwoFactorSecret()).isEqualTo("sec");
        verify(repository).save(user);
    }

    @Test
    void generateResetTokenSendsEmail() {
        User user = new User();
        user.setUsername("u");
        when(repository.findByUsername("u")).thenReturn(Optional.of(user));

        service.generateResetToken("u");

        verify(repository).save(user);
        verify(notification).sendToken(eq("u"), anyString());
        assertThat(user.getResetToken()).isNotNull();
        assertThat(user.getResetTokenExpiry()).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    @Test
    void generateResetTokenUserNotFound() {
        when(repository.findByUsername("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.generateResetToken("x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void resetPasswordFlow() {
        User user = new User();
        user.setResetToken("t");
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(1));
        when(repository.findByResetToken("t")).thenReturn(Optional.of(user));
        when(encoder.encode("n")).thenReturn("enc");

        service.resetPassword("t", "n");

        verify(repository).save(user);
        assertThat(user.getPassword()).isEqualTo("enc");
        assertThat(user.getResetToken()).isNull();
    }

    @Test
    void resetPasswordInvalidToken() {
        when(repository.findByResetToken("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.resetPassword("bad", "n"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void resetPasswordExpired() {
        User user = new User();
        user.setResetToken("t");
        user.setResetTokenExpiry(LocalDateTime.now().minusMinutes(1));
        when(repository.findByResetToken("t")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> service.resetPassword("t", "n"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void validateLoginToken() {
        User user = new User();
        user.setResetToken("t");
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(1));
        when(repository.findByResetToken("t")).thenReturn(Optional.of(user));

        User result = service.validateLoginToken("t");

        verify(repository).save(user);
        assertThat(result).isSameAs(user);
        assertThat(user.getResetToken()).isNull();
    }

    @Test
    void validateLoginTokenInvalid() {
        when(repository.findByResetToken("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.validateLoginToken("bad"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void validateLoginTokenExpired() {
        User user = new User();
        user.setResetToken("t");
        user.setResetTokenExpiry(LocalDateTime.now().minusMinutes(1));
        when(repository.findByResetToken("t")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> service.validateLoginToken("t"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void deleteDelegates() {
        service.delete(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void loadUserByUsername() {
        User user = new User();
        user.setUsername("u");
        user.setPassword("p");
        user.setRoles(Set.of(Role.ROLE_ADMIN));
        when(repository.findByUsername("u")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("u");
        assertThat(details.getUsername()).isEqualTo("u");
        assertThat(details.getAuthorities()).anySatisfy(a ->
                assertThat(a.getAuthority()).isEqualTo("ROLE_ADMIN"));
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(repository.findByUsername("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getByUsername() {
        User user = new User();
        when(repository.findByUsername("u")).thenReturn(Optional.of(user));
        assertThat(service.getByUsername("u")).isSameAs(user);
    }

    @Test
    void getByUsernameNotFound() {
        when(repository.findByUsername("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByUsername("x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void twoFactorDelegates() {
        when(twoFactor.generateSecret()).thenReturn("s");
        assertThat(service.generateTwoFactorSecret()).isEqualTo("s");
        when(twoFactor.verifyCode("s", 123456)).thenReturn(true);
        assertThat(service.verifyTwoFactorCode("s", 123456)).isTrue();
    }
}
