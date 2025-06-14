package com.fontolan.spring.securitykeyvault.example.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fontolan.spring.securitykeyvault.example.domain.model.AuthProvider;
import com.fontolan.spring.securitykeyvault.example.domain.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}


