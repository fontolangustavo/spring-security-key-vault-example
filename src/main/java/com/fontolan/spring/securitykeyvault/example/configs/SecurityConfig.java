package com.fontolan.spring.securitykeyvault.example.configs;

import com.fontolan.spring.securitykeyvault.example.auth.UserService;
import com.fontolan.spring.securitykeyvault.example.auth.jwt.JwtAuthenticationFilter;
import com.fontolan.spring.securitykeyvault.example.auth.oauth2.CustomOAuth2UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomOAuth2UserService oAuth2UserService;

    public SecurityConfig(UserService userService, JwtAuthenticationFilter jwtFilter,
                          CustomOAuth2UserService oAuth2UserService) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/products/**").hasAnyRole("USER","ADMIN")
                .anyRequest().permitAll())
            .userDetailsService(userService)
            .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(info -> info.userService(oAuth2UserService))
            );
        return http.build();
    }
}
