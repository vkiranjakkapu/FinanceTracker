package com.financetracker.identity.enums;

import com.platform.web.exception.ErrorDefinition;

public enum IdentityExceptions implements ErrorDefinition {

    // * Security Errors
    BAD_CREDENTIALS("BAD_CREDENTIALS", "SEC-4013", "Invalid Credentials Supplied."),

    // * User Errors
    USER_NOT_FOUND("USER_NOT_FOUND", "BUS-2002", "User with given details not found in records"),
    REGISTRATION_ERROR("REGISTRATION_ERROR", "BUS-2006", "Resource already exists in records."),
    DUPLICATE_RESOURCE_FOUND("DUPLICATE_RESOURCE_FOUND", "BUS-2007", "Resource already exists in records."),
    USER_NOT_DRIVER("USER_NOT_DRIVER", "BUS-2008", "You are not registered as a driver.");

    private final String errorName;
    private final String errorCode;
    private final String errorMessage;

    IdentityExceptions(String errorName,
            String errorCode,
            String errorMessage) {
        this.errorName = errorName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorName() {
        return this.errorName;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

}