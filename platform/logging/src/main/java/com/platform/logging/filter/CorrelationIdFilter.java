package com.platform.logging.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.platform.logging.constants.LoggingConstants;
import com.platform.logging.generator.CorrelationIdGenerator;
import com.platform.logging.holder.RequestContextHolder;
import com.platform.logging.properties.LoggingProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CorrelationIdFilter extends OncePerRequestFilter {

    private final CorrelationIdGenerator correlationIdGenerator;
    private final LoggingProperties properties;
    private final RequestContextHolder requestContextHolder;

    public CorrelationIdFilter(
            LoggingProperties properties,
            CorrelationIdGenerator correlationIdGenerator,
            RequestContextHolder requestContextHolder) {
        this.properties = properties;
        this.correlationIdGenerator = correlationIdGenerator;
        this.requestContextHolder = requestContextHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(properties.getCorrelationHeader());

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = correlationIdGenerator.generate(request);
        }

        requestContextHolder.put(LoggingConstants.CORRELATION_ID, correlationId);

        response.setHeader(properties.getCorrelationHeader(), correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            requestContextHolder.clear();
        }
    }
}