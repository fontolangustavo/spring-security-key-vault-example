package com.fontolan.spring.securitykeyvault.example.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fontolan.spring.securitykeyvault.example.domain.model.Token;

import java.util.Optional;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findByUsernameAndRevokedFalse(String username);
}


