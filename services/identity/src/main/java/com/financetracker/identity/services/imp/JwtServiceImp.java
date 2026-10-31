package com.financetracker.identity.services.imp;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.financetracker.identity.entities.User;
import com.financetracker.identity.services.JwtService;
import com.platform.security.properties.SecurityProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImp implements JwtService {

    private final SecurityProperties properties;

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(
                        properties.getJwt().getAuthoritiesClaim(),
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toList()))
                .claim(properties.getJwt().getEmailClaim(), user.getEmail())
                .claim(properties.getJwt().getUsernameClaim(), user.getFirstName() + " " + user.getLastName())
                .issuer(properties.getJwt().getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.getJwt().getAccessTokenExpiration())))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaims(token).get(properties.getJwt().getEmailClaim()).toString();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
