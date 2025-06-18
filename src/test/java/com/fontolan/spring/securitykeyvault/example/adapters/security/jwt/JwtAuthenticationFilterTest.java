package com.fontolan.spring.securitykeyvault.example.adapters.security.jwt;

import com.fontolan.spring.securitykeyvault.example.application.service.TokenService;
import com.fontolan.spring.securitykeyvault.example.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {
    private JwtUtil util;
    private TokenService tokenService;
    private UserService userService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        util = new JwtUtil("eJTkszxyBZtYaehPvTL/bP13pgQR1GCYoppvyRrLXgI=");
        tokenService = mock(TokenService.class);
        userService = mock(UserService.class);
        filter = new JwtAuthenticationFilter(util, tokenService, userService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void validTokenSetsAuthentication() throws ServletException, IOException {
        String token = util.generateToken("alice");
        when(tokenService.isTokenValid(token)).thenReturn(true);
        when(userService.loadUserByUsername("alice"))
                .thenReturn(new User("alice", "p", java.util.List.of()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("alice");
    }

    @Test
    void invalidTokenDoesNotAuthenticate() throws Exception {
        String token = util.generateToken("bob");
        when(tokenService.isTokenValid(token)).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(request, new MockHttpServletResponse(), chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
