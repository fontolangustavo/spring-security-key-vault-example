package com.fontolan.spring.securitykeyvault.example.adapters.web;

import com.fontolan.spring.securitykeyvault.example.adapters.security.jwt.JwtUtil;
import com.fontolan.spring.securitykeyvault.example.application.service.TokenService;
import com.fontolan.spring.securitykeyvault.example.application.service.UserDeviceService;
import com.fontolan.spring.securitykeyvault.example.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    TokenService tokenService;
    @MockBean
    UserService userService;
    @MockBean
    UserDeviceService deviceService;

    @Test
    void registerCallsService() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).save(any());
    }

    @Test
    void loginSuccess() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("u","p");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(deviceService.registerOrUpdate(eq("u"), any())).thenReturn(new com.fontolan.spring.securitykeyvault.example.domain.model.UserDevice());
        com.fontolan.spring.securitykeyvault.example.domain.model.User user = new com.fontolan.spring.securitykeyvault.example.domain.model.User();
        user.setTwoFactorEnabled(false);
        when(userService.getByUsername("u")).thenReturn(user);
        when(jwtUtil.generateToken("u")).thenReturn("tok");

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(tokenService).saveToken(eq("tok"), eq("u"), any());
    }

    @Test
    void loginTwoFactorFailure() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("u","p");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        com.fontolan.spring.securitykeyvault.example.domain.model.User user = new com.fontolan.spring.securitykeyvault.example.domain.model.User();
        user.setTwoFactorEnabled(true);
        user.setTwoFactorSecret("sec");
        when(userService.getByUsername("u")).thenReturn(user);
        when(userService.verifyTwoFactorCode("sec", 111111)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u\",\"password\":\"p\",\"code\":\"111111\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
