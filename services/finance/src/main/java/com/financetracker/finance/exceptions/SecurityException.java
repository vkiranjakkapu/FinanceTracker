package com.financetracker.finance.exceptions;

import com.platform.web.exception.ErrorDefinition;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SecurityException extends RuntimeException {

    private ErrorDefinition definition;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(ErrorDefinition definition) {
        this.definition = definition;
    }

    public SecurityException(ErrorDefinition definition, String message) {
        super(message);
        this.definition = definition;
    }

    public SecurityException(ErrorDefinition definition, String message, Throwable cause) {
        super(message, cause);
        this.definition = definition;
    }

}
