package com.platform.security.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.logging.constants.LoggingConstants;
import com.platform.logging.holder.RequestContextHolder;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthenticationContextInitializerFilterTest {

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private RequestContextHolder requestContextHolder;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AuthenticationContextInitializerFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationContextInitializerFilter(
                authenticationContext,
                requestContextHolder);
    }

    @Test
    void shouldPopulateRequestContext() throws Exception {

        AuthenticatedUser user = mock(AuthenticatedUser.class);

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(user));

        when(user.getUserId())
                .thenReturn("1234567890");

        when(user.getUsername())
                .thenReturn("venkat");

        filter.doFilter(request, response, filterChain);

        verify(requestContextHolder)
                .put(LoggingConstants.USER_ID, "1234567890");

        verify(requestContextHolder)
                .put(LoggingConstants.USERNAME, "venkat");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenUserIsAbsent() throws Exception {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        verifyNoInteractions(requestContextHolder);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldAlwaysContinueFilterChain() throws Exception {

        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);
    }
}