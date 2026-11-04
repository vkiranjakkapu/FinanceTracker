package com.financetracker.finance.exceptions;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalCommunicationException extends RuntimeException {

    private HttpStatusCode statusCode;

    public InternalCommunicationException(String message) {
        super(message);
    }

    public InternalCommunicationException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}