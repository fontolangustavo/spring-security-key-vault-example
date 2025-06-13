package com.fontolan.spring.securitykeyvault.example.auth.tokens;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isRevoked())
                .isPresent();
    }

    @Transactional
    public void revokeToken(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }
}
