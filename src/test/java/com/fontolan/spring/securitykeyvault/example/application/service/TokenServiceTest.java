package com.fontolan.spring.securitykeyvault.example.application.service;

import com.fontolan.spring.securitykeyvault.example.domain.model.Token;
import com.fontolan.spring.securitykeyvault.example.domain.model.UserDevice;
import com.fontolan.spring.securitykeyvault.example.domain.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TokenServiceTest {
    private TokenRepository repository;
    private TokenService service;

    @BeforeEach
    void setup() {
        repository = mock(TokenRepository.class);
        service = new TokenService(repository);
    }

    @Test
    void saveTokenWithoutDevice() {
        service.saveToken("tok", "user");
        ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
        verify(repository).save(captor.capture());
        Token saved = captor.getValue();
        assertThat(saved.getToken()).isEqualTo("tok");
        assertThat(saved.getUsername()).isEqualTo("user");
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getDevice()).isNull();
    }

    @Test
    void saveTokenWithDevice() {
        UserDevice device = new UserDevice();
        service.saveToken("tok", "user", device);
        ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
        verify(repository).save(captor.capture());
        Token saved = captor.getValue();
        assertThat(saved.getDevice()).isSameAs(device);
    }

    @Test
    void tokenValidation() {
        Token t = new Token();
        t.setToken("tok");
        t.setRevoked(false);
        when(repository.findByToken("tok")).thenReturn(Optional.of(t));
        assertThat(service.isTokenValid("tok")).isTrue();

        t.setRevoked(true);
        assertThat(service.isTokenValid("tok")).isFalse();
        when(repository.findByToken("missing")).thenReturn(Optional.empty());
        assertThat(service.isTokenValid("missing")).isFalse();
    }

    @Test
    void activeTokensDelegates() {
        List<Token> tokens = List.of(new Token());
        when(repository.findByUsernameAndRevokedFalse("u")).thenReturn(tokens);
        assertThat(service.activeTokens("u")).isEqualTo(tokens);
    }

    @Test
    void revokeToken() {
        Token token = new Token();
        when(repository.findByToken("t")).thenReturn(Optional.of(token));
        service.revokeToken("t");
        assertThat(token.isRevoked()).isTrue();
        verify(repository).save(token);
    }

    @Test
    void revokeTokenById() {
        Token token = new Token();
        when(repository.findById(1L)).thenReturn(Optional.of(token));
        service.revokeTokenById(1L);
        assertThat(token.isRevoked()).isTrue();
        verify(repository).save(token);
    }
}
