package com.platform.logging.generator;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

public final class DefaultCorrelationIdGenerator implements CorrelationIdGenerator {
    @Override
    public String generate(HttpServletRequest request) {
        return UUID.randomUUID().toString();
    }
}
