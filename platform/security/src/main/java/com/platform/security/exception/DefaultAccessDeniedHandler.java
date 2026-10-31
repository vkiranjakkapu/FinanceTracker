package com.platform.security.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class DefaultAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ExceptionLogger exceptionLogger;

    public DefaultAccessDeniedHandler(
            ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception)
            throws IOException {

        exceptionLogger.log(exception, request, HttpStatus.FORBIDDEN);

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden");
    }
}