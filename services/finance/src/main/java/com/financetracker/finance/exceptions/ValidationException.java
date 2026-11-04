package com.financetracker.finance.exceptions;

import java.util.List;

import com.platform.web.model.ValidationError;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException {
    private String field;
    private Object rejectedValue;
    private String validationMessage;
    private List<ValidationError> errors;

    public ValidationException(String field, Object rejectedValue,
            String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.validationMessage = message;
    }

    public ValidationException(List<ValidationError> errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return validationMessage;
    }

}
