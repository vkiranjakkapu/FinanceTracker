package com.financetracker.finance.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.exceptions.InternalCommunicationException;
import com.financetracker.finance.exceptions.SecurityException;
import com.financetracker.finance.exceptions.ValidationException;
import com.platform.web.exception.ErrorDefinition;
import com.platform.web.exception.SecurityExceptions;
import com.platform.web.exception.ValidationExceptions;
import com.platform.web.exception.WebExceptions;
import com.platform.web.model.ErrorResponse;
import com.platform.web.model.ValidationError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
		Optional.ofNullable(e.getCause()).ifPresent(er -> er.printStackTrace());
		ErrorDefinition definition = Optional.ofNullable(e.getDefinition())
				.orElse(SecurityExceptions.FORBIDDEN_ACCESS);
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse(definition,
						Optional.ofNullable(e.getMessage())
								.orElse(definition.getErrorMessage())));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		ErrorDefinition definition = Optional.ofNullable(e.getDefinition())
				.orElse(WebExceptions.APPLICATION_ERROR);

		return ResponseEntity.status(Optional.ofNullable(e.getStatus()).orElse(HttpStatus.BAD_REQUEST))
				.body(new ErrorResponse(definition,
						Optional.ofNullable(e.getMessage())
								.orElse(definition.getErrorMessage())));
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
		ErrorDefinition definition = ValidationExceptions.VALIDATION_ERROR;

		ErrorResponse errorResponse = new ErrorResponse(definition,
				Optional.ofNullable(e.getMessage()).orElse(definition.getErrorMessage()),
				List.of(new ValidationError(e.getField(), e.getRejectedValue(), e.getMessage())));

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler({ InternalCommunicationException.class })
	public ResponseEntity<?> handleInternalCommunicationExceptions(InternalCommunicationException e) {
		return ResponseEntity
				.status(Optional.ofNullable(e.getStatusCode()).orElse(HttpStatus.INTERNAL_SERVER_ERROR))
				.contentType(MediaType.APPLICATION_JSON)
				.body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception e) {
		e.printStackTrace();
		ErrorDefinition definition = WebExceptions.APPLICATION_ERROR;
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(definition,
						Optional.ofNullable(e.getMessage())
								.orElse(definition.getErrorMessage())));
	}

}
