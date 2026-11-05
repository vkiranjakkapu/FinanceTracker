package com.financetracker.finance.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.exceptions.InternalCommunicationException;
import com.financetracker.finance.exceptions.SecurityException;
import com.financetracker.finance.exceptions.ValidationException;
import com.platform.web.model.ErrorResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleSecurityException_ShouldReturnForbidden() {

        SecurityException exception =
                new SecurityException("Access denied");

        ResponseEntity<ErrorResponse> response =
                handler.handleSecurityException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleBusinessException_ShouldReturnConfiguredStatus() {

        BusinessException exception =
                new BusinessException(
                        com.platform.web.exception.WebExceptions.APPLICATION_ERROR,
                        "Resource conflict",
                        HttpStatus.CONFLICT);

        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleBusinessException_ShouldUseBadRequestWhenStatusIsMissing() {

        BusinessException exception =
                new BusinessException("Invalid business request");

        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {

        ValidationException exception =
                new ValidationException(
                        "amount",
                        -100,
                        "Amount must be positive");

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleInternalCommunicationException_ShouldReturnConfiguredStatus() {

        InternalCommunicationException exception =
                new InternalCommunicationException(
                        "Identity service unavailable",
                        HttpStatus.SERVICE_UNAVAILABLE);

        ResponseEntity<?> response =
                handler.handleInternalCommunicationExceptions(exception);

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode());

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Identity service unavailable", response.getBody());
    }

    @Test
    void handleUnexpectedException_ShouldReturnInternalServerError() {

        Exception exception = new Exception("Unexpected failure");

        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpectedExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}