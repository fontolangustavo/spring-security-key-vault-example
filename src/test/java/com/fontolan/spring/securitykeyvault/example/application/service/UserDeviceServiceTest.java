package com.fontolan.spring.securitykeyvault.example.application.service;

import com.fontolan.spring.securitykeyvault.example.domain.model.UserDevice;
import com.fontolan.spring.securitykeyvault.example.domain.repository.UserDeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserDeviceServiceTest {
    private UserDeviceRepository repository;
    private NotificationService notification;
    private UserDeviceService service;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        repository = mock(UserDeviceRepository.class);
        notification = mock(NotificationService.class);
        service = new UserDeviceService(repository, notification);
        request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("agent");
        when(request.getRemoteAddr()).thenReturn("ip");
    }

    @Test
    void createNewDevice() {
        when(repository.findByUsernameAndUserAgentAndIp("u", "agent", "ip"))
                .thenReturn(Optional.empty());
        UserDevice saved = new UserDevice();
        when(repository.save(any())).thenReturn(saved);

        UserDevice result = service.registerOrUpdate("u", request);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        verify(repository).save(captor.capture());
        UserDevice toSave = captor.getValue();
        assertThat(toSave.getUsername()).isEqualTo("u");
        assertThat(toSave.isTrusted()).isFalse();
        verify(notification).sendSuspiciousLogin("u", "agent" + "ip");
    }

    @Test
    void updateExistingDevice() {
        UserDevice existing = new UserDevice();
        existing.setTrusted(true);
        when(repository.findByUsernameAndUserAgentAndIp("u", "agent", "ip"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        UserDevice result = service.registerOrUpdate("u", request);
        assertThat(result).isSameAs(existing);
        assertThat(existing.getLastUsed()).isNotNull();
        verify(notification, never()).sendSuspiciousLogin(anyString(), anyString());
    }

    @Test
    void listAndTrusted() {
        List<UserDevice> list = List.of(new UserDevice());
        when(repository.findByUsername("u")).thenReturn(list);
        assertThat(service.listDevices("u")).isEqualTo(list);

        UserDevice trusted = new UserDevice();
        trusted.setTrusted(true);
        when(repository.findByUsernameAndUserAgentAndIp("u", "agent", "ip"))
                .thenReturn(Optional.of(trusted));
        assertThat(service.isTrustedDevice("u", request)).isTrue();
    }
}
