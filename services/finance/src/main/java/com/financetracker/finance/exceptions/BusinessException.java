package com.financetracker.finance.exceptions;

import org.springframework.http.HttpStatus;

import com.platform.web.exception.ErrorDefinition;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private ErrorDefinition definition;
    private HttpStatus status;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ErrorDefinition definition) {
        this.definition = definition;
    }

    public BusinessException(ErrorDefinition definition, String message) {
        super(message);
        this.definition = definition;
    }

    public BusinessException(ErrorDefinition definition, HttpStatus status) {
        this.definition = definition;
        this.status = status;
    }

    public BusinessException(ErrorDefinition definition, String message, HttpStatus status) {
        super(message);
        this.definition = definition;
        this.status = status;
    }

}
