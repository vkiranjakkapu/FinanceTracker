package com.financetracker.finance.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.financetracker.finance.exceptions.BusinessException;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.security.model.DefaultAuthenticatedUser;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceImpTest {

    private static final String USER_ID =
            "c0186249-9fc1-4927-97b3-a08a21febfe3";

    @Mock
    private AuthenticationContext authenticationContext;

    @InjectMocks
    private CurrentUserServiceImp currentUserService;

    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new DefaultAuthenticatedUser(
                USER_ID,
                "kiran",
                "kiran@example.com",
                List.of("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    void currentUser_ShouldReturnAuthenticatedUser() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        assertSame(authenticatedUser, currentUserService.currentUser());
    }

    @Test
    void currentUser_ShouldThrowWhenNoUserIsAuthenticated() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> currentUserService.currentUser());

        assertEquals("No authenticated user", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void userId_ShouldReturnAuthenticatedUserId() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        assertEquals(UUID.fromString(USER_ID), currentUserService.userId());
    }

    @Test
    void userId_ShouldRejectInvalidUuid() {

        AuthenticatedUser invalidUser = new DefaultAuthenticatedUser(
                "invalid-uuid",
                "kiran",
                "kiran@example.com",
                List.of("ROLE_USER"));

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(invalidUser));

        assertThrows(
                IllegalArgumentException.class,
                () -> currentUserService.userId());
    }

    @Test
    void username_ShouldReturnAuthenticatedUsername() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        assertEquals("kiran", currentUserService.username());
    }

    @Test
    void email_ShouldReturnAuthenticatedEmail() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        assertEquals("kiran@example.com", currentUserService.email());
    }

    @Test
    void isAdmin_ShouldReturnTrueWhenUserHasAdminRole() {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        assertTrue(currentUserService.isAdmin());
    }

    @Test
    void isAdmin_ShouldReturnFalseWhenUserDoesNotHaveAdminRole() {

        AuthenticatedUser regularUser = new DefaultAuthenticatedUser(
                USER_ID,
                "kiran",
                "kiran@example.com",
                List.of("ROLE_USER"));

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(regularUser));

        assertFalse(currentUserService.isAdmin());
    }

    @Test
    void isRider_ShouldReturnTrueWhenUserHasRiderRole() {

        AuthenticatedUser rider = new DefaultAuthenticatedUser(
                USER_ID,
                "kiran",
                "kiran@example.com",
                List.of("ROLE_RIDER"));

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(rider));

        assertTrue(currentUserService.isRider());
    }

    @Test
    void isDriver_ShouldReturnTrueWhenUserHasDriverRole() {

        AuthenticatedUser driver = new DefaultAuthenticatedUser(
                USER_ID,
                "kiran",
                "kiran@example.com",
                List.of("ROLE_DRIVER"));

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(driver));

        assertTrue(currentUserService.isDriver());
    }
}