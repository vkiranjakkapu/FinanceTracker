package com.platform.web.model;

import java.time.LocalDateTime;
import java.util.List;

import com.platform.web.exception.WebExceptions;
import com.platform.web.exception.ErrorDefinition;

public record ErrorResponse(

        String errorName,

        String errorCode,

        String errorMessage,

        List<ValidationError> validationErrors,

        LocalDateTime timestamp) {

    public ErrorResponse() {
        this(WebExceptions.APPLICATION_ERROR);
    }

    public ErrorResponse(ErrorDefinition error) {
        this(
                error.getErrorName(),
                error.getErrorCode(),
                error.getErrorMessage(),
                null,
                LocalDateTime.now());
    }

    public ErrorResponse(
            ErrorDefinition error,
            String errorMessage) {

        this(
                error.getErrorName(),
                error.getErrorCode(),
                errorMessage,
                null,
                LocalDateTime.now());
    }

    public ErrorResponse(
            ErrorDefinition error,
            String errorMessage,
            List<ValidationError> validationErrors) {

        this(
                error.getErrorName(),
                error.getErrorCode(),
                errorMessage,
                validationErrors,
                LocalDateTime.now());
    }

    public ErrorResponse(
            String errorName,
            String errorCode,
            String errorMessage) {

        this(
                errorName,
                errorCode,
                errorMessage,
                null,
                LocalDateTime.now());
    }
}