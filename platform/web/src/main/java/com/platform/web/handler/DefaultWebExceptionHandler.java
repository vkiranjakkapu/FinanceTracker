package com.platform.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.platform.web.exception.SecurityExceptions;
import com.platform.web.exception.ValidationExceptions;
import com.platform.web.exception.WebExceptions;
import com.platform.web.model.ErrorResponse;
import com.platform.web.model.ValidationError;

@RestControllerAdvice
public class DefaultWebExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException exception) {
		List<ValidationError> validationErrors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> new ValidationError(
						error.getField(),
						error.getRejectedValue(),
						error.getDefaultMessage()))
				.toList();

		return ResponseEntity.badRequest()
				.body(new ErrorResponse(
						ValidationExceptions.VALIDATION_ERROR,
						"Validation failed.",
						validationErrors));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(
			BindException exception) {
		List<ValidationError> validationErrors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> new ValidationError(
						error.getField(),
						error.getRejectedValue(),
						error.getDefaultMessage()))
				.toList();

		return ResponseEntity.badRequest()
				.body(new ErrorResponse(
						ValidationExceptions.VALIDATION_ERROR,
						"Validation failed.",
						validationErrors));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException exception) {

		return ResponseEntity
				.badRequest()
				.body(new ErrorResponse(
						WebExceptions.BAD_REQUEST,
						exception.getMessage()));
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
			HttpClientErrorException exception) {

		return ResponseEntity
				.badRequest()
				.body(new ErrorResponse(
						SecurityExceptions.UNAUTHORIZED_INTERNAL_ACCESS,
						exception.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException exception) {

		return ResponseEntity
				.badRequest()
				.body(new ErrorResponse(
						WebExceptions.BAD_REQUEST,
						"Malformed request body."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
			IllegalArgumentException exception) {

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(
						WebExceptions.BAD_REQUEST,
						exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception) {

		return ResponseEntity
				.badRequest()
				.body(new ErrorResponse(
						WebExceptions.BAD_REQUEST,
						String.format(
								"Invalid value '%s' for parameter '%s'.",
								exception.getValue(),
								exception.getName())));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnhandledException(
			Exception exception) {

		return ResponseEntity
				.internalServerError()
				.body(new ErrorResponse(
						WebExceptions.INTERNAL_SERVER_ERROR, exception.getMessage()));
	}
}