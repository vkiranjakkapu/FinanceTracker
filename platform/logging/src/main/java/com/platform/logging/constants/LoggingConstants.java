package com.platform.logging.constants;

import org.springframework.core.Ordered;

public final class LoggingConstants {

    public static final String CORRELATION_ID = "correlationId";
    public static final String CORRELATION_HEADER = "X-Correlation-Id";

    public static final String USER_ID = "userId";
    public static final String USER_HEADER = "X-User-Id";
    public static final String USERNAME = "username";
    
    public static final int HIGHEST_PRECEDENCE_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;
    public static final int CORRELATION_FILTER_ORDER = HIGHEST_PRECEDENCE_FILTER_ORDER;
    public static final int REQUEST_LOGGING_FILTER_ORDER = CORRELATION_FILTER_ORDER + 1;

}