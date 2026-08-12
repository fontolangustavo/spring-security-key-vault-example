package com.fontolan.spring.securitykeyvault.example.adapters.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {
    private static final String SECRET = "eJTkszxyBZtYaehPvTL/bP13pgQR1GCYoppvyRrLXgI=";

    @Test
    void generateAndParseToken() {
        JwtUtil util = new JwtUtil(SECRET);
        String token = util.generateToken("alice");
        assertThat(util.getUsername(token)).isEqualTo("alice");
        assertThat(util.isExpired(token)).isFalse();
    }

    @Test
    void expiredTokenIsDetected() {
        byte[] bytes = Decoders.BASE64.decode(SECRET);
        Key key = Keys.hmacShaKeyFor(bytes);
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject("bob")
                .setIssuedAt(new Date(now.getTime() - 7200_000))
                .setExpiration(new Date(now.getTime() - 3600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        JwtUtil util = new JwtUtil(SECRET);
        assertThat(util.getUsername(token)).isEqualTo("bob");
        assertThat(util.isExpired(token)).isTrue();
    }
}
