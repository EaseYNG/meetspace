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

    public String generateUserToken(UserDTO userDTO) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Map<String, Object> claim = new HashMap<>();

        claim.put("username", userDTO.getUsername());

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setClaims(claim)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public User getUserFromClaims(Claims claim) {
        User temp = new User();

        temp.setUsername(claim.get("username", String.class));
        temp.setPassword(claim.get("password", String.class));
//        temp.setNickname(claim.get("nickname", String.class));
//        temp.setAge(claim.get("age", Integer.class));
//        temp.setGender(claim.get("gender", String.class));
//        temp.setEmail(claim.get("email", String.class));
//        temp.setFirstname(claim.get("firstname", String.class));
//        temp.setLastname(claim.get("lastname", String.class));

        return temp;
    }

    public User getUserFromToken(String token) {
        return getUserFromClaims(getClaimsFromToken(token));
    }
}
