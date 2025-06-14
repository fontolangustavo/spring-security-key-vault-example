package com.fontolan.spring.securitykeyvault.example.controllers;

import com.fontolan.spring.securitykeyvault.example.auth.jwt.JwtUtil;
import com.fontolan.spring.securitykeyvault.example.auth.tokens.TokenService;
import com.fontolan.spring.securitykeyvault.example.auth.UserService;
import com.fontolan.spring.securitykeyvault.example.auth.devices.UserDeviceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final UserService userService;
    private final UserDeviceService deviceService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          TokenService tokenService, UserService userService,
                          UserDeviceService deviceService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.userService = userService;
        this.deviceService = deviceService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password")));
        String username = auth.getName();
        var device = deviceService.registerOrUpdate(username, request);

        var user = userService.getByUsername(username);
        if (user.isTwoFactorEnabled()) {
            String codeStr = body.get("code");
            int code;
            try {
                code = Integer.parseInt(codeStr);
            } catch (Exception ex) {
                return ResponseEntity.status(401).build();
            }
            if (!userService.verifyTwoFactorCode(user.getTwoFactorSecret(), code)) {
                return ResponseEntity.status(401).build();
            }
        }

        String token = jwtUtil.generateToken(username);
        tokenService.saveToken(token, username, device);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/reset/request")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> body) {
        userService.generateResetToken(body.get("username"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<?> confirmReset(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("token"), body.get("password"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-token")
    public ResponseEntity<?> loginWithToken(@RequestBody Map<String, String> body, HttpServletRequest request) {
        var user = userService.validateLoginToken(body.get("token"));
        var device = deviceService.registerOrUpdate(user.getUsername(), request);
        String token = jwtUtil.generateToken(user.getUsername());
        tokenService.saveToken(token, user.getUsername(), device);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String oldToken = header.substring(7);
            if (!jwtUtil.isExpired(oldToken) && tokenService.isTokenValid(oldToken)) {
                String username = jwtUtil.getUsername(oldToken);
                tokenService.revokeToken(oldToken);
                String newToken = jwtUtil.generateToken(username);
                tokenService.saveToken(newToken, username);
                return ResponseEntity.ok(Map.of("token", newToken));
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            tokenService.revokeToken(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/devices")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> listDevices(Authentication authentication) {
        return ResponseEntity.ok(tokenService.activeTokens(authentication.getName()));
    }

    @PostMapping("/devices/{id}/revoke")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> revokeDevice(@PathVariable Long id) {
        tokenService.revokeTokenById(id);
        return ResponseEntity.ok().build();
    }
}
