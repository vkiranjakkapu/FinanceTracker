package com.platform.security.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class DefaultAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ExceptionLogger exceptionLogger;

    public DefaultAuthenticationEntryPoint(
            ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException {

        exceptionLogger.log(exception, request, HttpStatus.UNAUTHORIZED);

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized");
    }
}