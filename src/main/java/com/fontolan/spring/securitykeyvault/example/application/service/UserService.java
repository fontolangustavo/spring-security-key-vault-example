package com.fontolan.spring.securitykeyvault.example.application.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fontolan.spring.securitykeyvault.example.application.service.NotificationService;
import com.fontolan.spring.securitykeyvault.example.domain.repository.UserRepository;
import com.fontolan.spring.securitykeyvault.example.domain.model.User;
import com.fontolan.spring.securitykeyvault.example.domain.model.Role;
import com.fontolan.spring.securitykeyvault.example.application.service.TwoFactorAuthService;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final TwoFactorAuthService twoFactorAuthService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       NotificationService notificationService, TwoFactorAuthService twoFactorAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isTwoFactorEnabled() && (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isEmpty())) {
            user.setTwoFactorSecret(generateTwoFactorSecret());
        }
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void generateResetToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        notificationService.sendToken(user.getUsername(), token);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UsernameNotFoundException("Token expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public User validateLoginToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UsernameNotFoundException("Token expired");
        }
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return user;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(java.util.Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String generateTwoFactorSecret() {
        return twoFactorAuthService.generateSecret();
    }

    public boolean verifyTwoFactorCode(String secret, int code) {
        return twoFactorAuthService.verifyCode(secret, code);
    }
}

