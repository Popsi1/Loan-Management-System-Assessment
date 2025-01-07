package com.example.usermodule.config.securityConfig;

import com.example.usermodule.entity.LoanUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtility implements Serializable {

        private static final long serialVersionUID = 234234523523L;

        // Token validity in milliseconds (5 hours)
        private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

        // Secret key from application.properties
        @Value("${jwt.secret}")
        private String secret;

        private Key getSigningKey() {
                // Ensure the secret is converted to a secure Key object
                byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
                return Keys.hmacShaKeyFor(keyBytes);
        }

        // Retrieve the username (email) from the token
        public String getUserEmailFromToken(String token) {
                return getClaimFromToken(token, Claims::getSubject);
        }

        // Generate a new token for the user
        public String generateToken(LoanUser loanUser) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", loanUser.getRole());
                claims.put("user-id", String.valueOf(loanUser.getId()));
                return doGenerateToken(claims, loanUser.getEmail());
        }

        // Build the token with claims, subject, and expiration
        private String doGenerateToken(Map<String, Object> claims, String subject) {
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                        .compact();
        }

        // Retrieve a specific claim from the token
        public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
                final Claims claims = getAllClaimsFromToken(token);
                return claimsResolver.apply(claims);
        }

        // Parse all claims from the token
        private Claims getAllClaimsFromToken(String token) {
                return Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        }

        // Check if the token has expired
        public Boolean isTokenExpired(String token) {
                final Date expiration = getExpirationDateFromToken(token);
                return expiration.before(new Date());
        }

        // Get the expiration date from the token
        public Date getExpirationDateFromToken(String token) {
                return getClaimFromToken(token, Claims::getExpiration);
        }

        // Validate if the token matches the user and is not expired
        public Boolean validateToken(String token, UserDetails userDetails) {
                final String username = getUserEmailFromToken(token);
                return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }

        // Check if the token can be refreshed
        public Boolean canTokenBeRefreshed(String token) {
                return !isTokenExpired(token);
        }
}
