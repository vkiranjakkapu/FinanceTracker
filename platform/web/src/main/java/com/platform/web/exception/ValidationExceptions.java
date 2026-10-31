package com.platform.web.exception;

public enum ValidationExceptions implements ErrorDefinition {

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            "VAL-4001",
            "Validation failed.");

    private final String errorName;

    private final String errorCode;

    private final String errorMessage;

    ValidationExceptions(
            String errorName,
            String errorCode,
            String errorMessage) {

        this.errorName = errorName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorName() {
        return errorName;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}