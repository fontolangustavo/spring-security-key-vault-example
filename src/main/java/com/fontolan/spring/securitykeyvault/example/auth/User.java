package com.fontolan.spring.securitykeyvault.example.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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

    // Comma separated roles, e.g. "ROLE_USER,ROLE_ADMIN"
    private String roles;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    // Secret key used for TOTP based 2FA
    private String twoFactorSecret;

    // Flag indicating if 2FA is enabled for this user
    private boolean twoFactorEnabled;
}
