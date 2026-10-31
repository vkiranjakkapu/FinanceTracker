package com.platform.security.exception;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationEntryPointTest {

    @Mock
    private ExceptionLogger exceptionLogger;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authenticationException;

    private DefaultAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new DefaultAuthenticationEntryPoint(exceptionLogger);
    }

    @Test
    void shouldReturnUnauthorized() throws Exception {

        entryPoint.commence(
                request,
                response,
                authenticationException);

        verify(exceptionLogger)
                .log(
                        authenticationException,
                        request,
                        HttpStatus.UNAUTHORIZED);

        verify(response)
                .sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized");

        verifyNoMoreInteractions(exceptionLogger, response);
    }
}