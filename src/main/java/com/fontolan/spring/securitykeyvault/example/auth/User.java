package com.fontolan.spring.securitykeyvault.example.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

import com.fontolan.spring.securitykeyvault.example.auth.Role;

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
}
