package com.platform.web.exception;

public enum WebExceptions implements ErrorDefinition {

    APPLICATION_ERROR(
            "APPLICATION_ERROR",
            "APP-0000",
            "Application error."),

    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            "WEB-5000",
            "An unexpected error occurred."),

    BAD_REQUEST(
            "BAD_REQUEST",
            "WEB-4000",
            "Bad request."),

    RESOURCE_NOT_FOUND(
            "RESOURCE_NOT_FOUND",
            "WEB-4040",
            "Requested resource was not found.");

    private final String errorName;

    private final String errorCode;

    private final String errorMessage;

    WebExceptions(
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
