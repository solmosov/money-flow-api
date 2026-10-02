package com.example.moneyflowapi.security.jwt;

import com.example.moneyflowapi.security.jwt.dto.GenerateAccessTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    public static final String USER_EMAIL = "user_email";
    public static final String CUSTOMER_ID = "customer_id";

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public String generateAccessToken(GenerateAccessTokenDto dto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "ACCESS_TOKEN");
        claims.put(USER_EMAIL, dto.userEmail());

        if(dto.customerId() != null){
            claims.put(CUSTOMER_ID, dto.customerId());
        }

        return buildToken(dto.userId(), claims, this.accessTokenExpiration);
    }


    public Claims parseToken(String jwt) {
        return extractClaims(jwt);
    }


    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }


    private String buildToken(UUID userId, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .subject(userId.toString())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new JwtException("Invalid JWT token", ex);
        }
    }


    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
