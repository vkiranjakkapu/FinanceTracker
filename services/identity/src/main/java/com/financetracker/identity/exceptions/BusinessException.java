package com.financetracker.identity.exceptions;

import com.platform.web.exception.ErrorDefinition;

public class BusinessException extends RuntimeException {

    private ErrorDefinition exception;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
    }

    public BusinessException(ErrorDefinition exceptions, String message) {
        super(message);
        this.exception = exceptions;
    }

    public BusinessException(ErrorDefinition exceptions, String message, Throwable e) {
        super(message, e);
        this.exception = exceptions;
    }

    public ErrorDefinition getException() {
        return exception;
    }

    public void setException(ErrorDefinition exceptions) {
        this.exception = exceptions;
    }

}
