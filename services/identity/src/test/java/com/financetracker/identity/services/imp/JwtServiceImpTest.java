package com.financetracker.identity.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.financetracker.identity.entities.Role;
import com.financetracker.identity.entities.RoleType;
import com.financetracker.identity.entities.User;
import com.platform.security.properties.SecurityProperties;

import io.jsonwebtoken.JwtException;

class JwtServiceImpTest {

    private JwtServiceImp jwtService;

    private SecurityProperties properties;

    private User user;

    private UUID USER_ID;

    @BeforeEach
    void setup() {

        USER_ID = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

        properties = new SecurityProperties();

        properties.getJwt().setSecret(
                "ThisIsAVeryLongSecretKeyForJwtTestingPurposeOnly123456789");

        properties.getJwt().setAccessTokenExpiration(Duration.ofHours(1));

        properties.getJwt().setRefreshTokenExpiration(Duration.ofDays(1));

        jwtService = new JwtServiceImp(properties);

        Role role = new Role();
        role.setName(RoleType.ADMIN);

        user = User.builder()
                .id(USER_ID)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .roles(Set.of(role))
                .build();
    }

    @Test
    void generateAccessToken_ShouldReturnToken() {

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {

        String token = jwtService.generateAccessToken(user);

        String email = jwtService.extractEmail(token);

        assertEquals("admin@test.com", email);
    }

    @Test
    void generateRefreshToken_ShouldGenerateUniqueTokens() {

        String token1 = jwtService.generateRefreshToken();
        String token2 = jwtService.generateRefreshToken();

        assertNotEquals(token1, token2);
    }

    @Test
    void isTokenValid_ShouldReturnTrue() {

        String token = jwtService.generateAccessToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenDifferentUser() {

        String token = jwtService.generateAccessToken(user);

        UserDetails userDetails = User.builder()
                .email("another@test.com")
                .password("password")
                .roles(user.getRoles())
                .build();

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void extractEmail_ShouldThrow_WhenTokenInvalid() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractEmail("invalid-token"));
    }

}
