package com.fontolan.spring.securitykeyvault.example.controllers;

import com.fontolan.spring.securitykeyvault.example.auth.jwt.JwtUtil;
import com.fontolan.spring.securitykeyvault.example.auth.tokens.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password")));
        String username = auth.getName();
        String token = jwtUtil.generateToken(username);
        tokenService.saveToken(token, username);
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
}
