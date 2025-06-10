package com.scentpapa.scentpapa_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    // Generate JWT token with subject and role claim
    public String generateToken(String email, String role) {
        Key key = getSigningKey();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claims(Map.of("role", role))
                .signWith(key)
                .compact();
    }

    // Validate token
    public boolean isTokenValid(String token) {
        try {
            parseToken(token); // Will throw if invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extract username (subject)
    public String extractUsername(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    // Extract role
    public String extractRole(String token) {
        Object role = parseToken(token).getPayload().get("role");
        return role != null ? role.toString() : null;
    }

    // Parse token
    private Jws<Claims> parseToken(String token) {
        SecretKey key = getSigningKey();

        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        return parser.parseSignedClaims(token);
    }

    // Get signing key from secret
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
