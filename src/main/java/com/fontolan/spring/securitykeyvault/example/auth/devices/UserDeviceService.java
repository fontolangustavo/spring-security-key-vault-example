package com.fontolan.spring.securitykeyvault.example.auth.devices;

import com.fontolan.spring.securitykeyvault.example.services.NotificationService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class UserDeviceService {
    private final UserDeviceRepository repository;
    private final NotificationService notificationService;

    public UserDeviceService(UserDeviceRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public UserDevice registerOrUpdate(String username, HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        UserDevice device = repository.findByUsernameAndUserAgentAndIp(username, agent, ip)
                .orElseGet(() -> {
                    UserDevice d = new UserDevice();
                    d.setUsername(username);
                    d.setUserAgent(agent);
                    d.setIp(ip);
                    d.setDeviceName(agent);
                    d.setTrusted(false);
                    notificationService.sendSuspiciousLogin(username, agent + " " + ip);
                    return d;
                });
        device.setLastUsed(LocalDateTime.now());
        return repository.save(device);
    }

    public java.util.List<UserDevice> listDevices(String username) {
        return repository.findByUsername(username);
    }

    public boolean isTrustedDevice(String username, HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        return repository.findByUsernameAndUserAgentAndIp(username, agent, ip)
                .map(UserDevice::isTrusted)
                .orElse(false);
    }
}
