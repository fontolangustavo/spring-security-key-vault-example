package com.fontolan.spring.securitykeyvault.example.auth.devices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByUsernameAndUserAgentAndIp(String username, String userAgent, String ip);
    List<UserDevice> findByUsername(String username);
}
