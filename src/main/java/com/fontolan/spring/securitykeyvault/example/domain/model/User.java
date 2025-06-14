package com.fontolan.spring.securitykeyvault.example.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

import com.fontolan.spring.securitykeyvault.example.domain.model.Role;
import com.fontolan.spring.securitykeyvault.example.domain.model.AuthProvider;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    // Secret key used for TOTP based 2FA
    private String twoFactorSecret;

    // Flag indicating if 2FA is enabled for this user
    private boolean twoFactorEnabled;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    private String providerId;
}
