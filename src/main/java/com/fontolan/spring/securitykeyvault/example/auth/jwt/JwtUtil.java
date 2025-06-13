package com.fontolan.spring.securitykeyvault.example.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;
    private static final long EXPIRATION_MS = 3600_000; // 1 hour

    public JwtUtil() {
        // In real apps use a stronger key and load from config
        byte[] secret = Decoders.BASE64.decode("YXdzZW9tZWhzZWNyZXQxMjM0NQ==");
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
