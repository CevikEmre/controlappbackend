package com.noronsoft.noroncontrolapp.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey ACCESS_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final SecretKey REFRESH_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Extract the username from the token
    public String extractUsername(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getSubject, isRefreshToken);
    }

    // Extract the userId from the token
    public Integer extractUserId(String token, boolean isRefreshToken) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId != null) {
                return Integer.parseInt(userId.toString());
            }
            throw new IllegalArgumentException("userId claim is missing from the token");
        }, isRefreshToken);
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getExpiration, isRefreshToken);
    }

    // General method to extract claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean isRefreshToken) {
        final Claims claims = extractAllClaims(token, isRefreshToken);
        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        SecretKey key = isRefreshToken ? REFRESH_SECRET_KEY : ACCESS_SECRET_KEY;
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token, boolean isRefreshToken) {
        return extractExpiration(token, isRefreshToken).before(new Date());
    }

    // Access token generation
    public String generateAccessToken(String username, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // Add user ID to token
        return createToken(claims, username, 1000 * 60 * 60 * 24, false); // 24 hours expiration
    }

    // Refresh token generation
    public String generateRefreshToken(String username, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // Add userId to the claims
        return createToken(claims, username, 1000 * 60 * 60 * 24 * 7, true); // 7 days expiry for refresh token
    }

    // Token creation method
    private String createToken(Map<String, Object> claims, String subject, long expirationTime, boolean isRefreshToken) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(isRefreshToken ? REFRESH_SECRET_KEY : ACCESS_SECRET_KEY) // Use the appropriate key
                .compact();
    }

    // Token validation
    public Boolean validateToken(String token, String username, boolean isRefreshToken) {
        final String extractedUsername = extractUsername(token, isRefreshToken);
        return (extractedUsername.equals(username) && !isTokenExpired(token, isRefreshToken));
    }

    // Refresh token validation
    public Boolean validateRefreshToken(String token, String username) {
        return validateToken(token, username, true);
    }
}
