package com.platform.web.exception;

public enum SecurityExceptions implements ErrorDefinition {
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "SEC-4010", "Unauthorized Access Attempted."),
    UNAUTHORIZED_INTERNAL_ACCESS("UNAUTHORIZED_INTERNAL_ACCESS", "SEC-4011", "Unauthorized Access Attempted By Service."),
    FORBIDDEN_ACCESS("FORBIDDEN_ACCESS", "SEC-4031", "Access Denied."),

    INVALID_TOKEN("INVALID_REFRESH_TOKEN", "SEC-4012", "Token Provided Was Invalid.");

    private final String errorName;
    private final String errorCode;
    private final String errorMessage;

    private SecurityExceptions(String errorName,
            String errorCode,
            String errorMessage) {
        this.errorName = errorName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

