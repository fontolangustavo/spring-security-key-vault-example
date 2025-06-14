package com.fontolan.spring.securitykeyvault.example.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_devices")
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String userAgent;
    private String ip;
    private String deviceName;

    private boolean trusted;

    private LocalDateTime lastUsed;
}
