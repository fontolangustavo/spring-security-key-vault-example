package com.fontolan.spring.securitykeyvault.example.auth.tokens;

import jakarta.persistence.*;
import lombok.Data;
import com.fontolan.spring.securitykeyvault.example.auth.devices.UserDevice;

@Data
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private String username;

    private boolean revoked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id")
    private UserDevice device;
}
