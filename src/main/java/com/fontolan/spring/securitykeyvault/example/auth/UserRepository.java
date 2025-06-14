package com.fontolan.spring.securitykeyvault.example.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fontolan.spring.securitykeyvault.example.auth.AuthProvider;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
