package com.fontolan.spring.securitykeyvault.example.auth.tokens;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fontolan.spring.securitykeyvault.example.auth.devices.UserDevice;
import java.util.List;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void saveToken(String token, String username) {
        Token t = new Token();
        t.setToken(token);
        t.setUsername(username);
        t.setRevoked(false);
        tokenRepository.save(t);
    }

    @Transactional
    public void saveToken(String token, String username, UserDevice device) {
        Token t = new Token();
        t.setToken(token);
        t.setUsername(username);
        t.setDevice(device);
        t.setRevoked(false);
        tokenRepository.save(t);
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isRevoked())
                .isPresent();
    }

    public List<Token> activeTokens(String username) {
        return tokenRepository.findByUsernameAndRevokedFalse(username);
    }

    @Transactional
    public void revokeToken(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }

    @Transactional
    public void revokeTokenById(Long id) {
        tokenRepository.findById(id).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }
}
