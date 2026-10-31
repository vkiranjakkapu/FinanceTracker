package com.platform.logging.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.platform.logging.properties.LoggingProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final LoggingProperties properties;

    public RequestLoggingFilter(
            LoggingProperties properties) {

        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        logIncomingRequest(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            logOutgoingResponse(request, response, duration);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        return properties.getRequest().getExcludedPaths()
                .stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, requestPath));
    }

    private void logIncomingRequest(HttpServletRequest request) {
        StringBuilder message = new StringBuilder();
        message.append("REQ ")
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURI());
        if (properties.getRequest().isIncludeQueryString()
                && request.getQueryString() != null) {
            message.append("?")
                    .append(request.getQueryString());
        }

        if (properties.getRequest().isIncludeClientIp()) {
            message.append(" | client=")
                    .append(request.getRemoteAddr());
        }

        log.info(message.toString());
    }

    private void logOutgoingResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            long duration) {
        log.info(
                "RES {} {} | status={} | {} ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }
}