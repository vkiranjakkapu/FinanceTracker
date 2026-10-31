package com.platform.logging.generator;

import jakarta.servlet.http.HttpServletRequest;

public interface CorrelationIdGenerator {
    String generate(HttpServletRequest request);
}
