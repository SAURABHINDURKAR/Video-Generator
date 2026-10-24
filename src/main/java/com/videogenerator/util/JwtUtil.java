package com.videogenerator.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:VideoGeneratorSecretKeyMustBeAtLeast256BitsLongForHS256AlgorithmToWorkProperly}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpirationMs; // Default: 24 hours

    /**
     * Generate JWT token for a user
     */
    public String generateToken(String username) {
        log.info("Generating JWT token for user: {}", username);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        
        return createToken(claims, username);
    }

    /**
     * Generate JWT token with additional claims
     */
    public String generateTokenWithClaims(String username, Map<String, Object> claims) {
        log.info("Generating JWT token with claims for user: {}", username);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.debug("JWT token created successfully");
        return token;
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get expiration date from JWT token
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Error extracting expiration date from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get all claims from JWT token
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if JWT token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validate JWT token
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            
            log.debug("JWT token validation successful");
            return true;
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token and check if it's not expired
     */
    public Boolean validateTokenAndNotExpired(String token) {
        return validateToken(token) && !isTokenExpired(token);
    }

    /**
     * Get claim from token
     */
    public Object getClaimFromToken(String token, String claimName) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get(claimName);
        } catch (Exception e) {
            log.error("Error extracting claim from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if token is valid for user
     */
    public Boolean isTokenValid(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
}
