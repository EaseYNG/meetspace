package com.venus.meetspace.util;

import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private final String secret = "your-256-bit-secret-key-123456789012345678901234";
    private long expiration = 1000*60*60; // 1 hour

    public String generateUserToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Map<String, Object> claim = new HashMap<>();

        claim.put("id", user.getId());
        claim.put("username", user.getUsername());

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setClaims(claim)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 从token获取claims (implements Map)
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("id", Long.class);
    }
}
