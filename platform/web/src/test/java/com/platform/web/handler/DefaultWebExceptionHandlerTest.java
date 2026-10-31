package com.platform.web.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.platform.web.exception.ValidationExceptions;
import com.platform.web.exception.WebExceptions;
import com.platform.web.model.ErrorResponse;

class DefaultWebExceptionHandlerTest {

	private DefaultWebExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new DefaultWebExceptionHandler();
	}

	@Test
	void shouldHandleMethodArgumentNotValidException() {

		BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");

		bindingResult.addError(
				new FieldError(
						"request",
						"email",
						"abc",
						false,
						null,
						null,
						"must be a valid email"));

		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
				mock(MethodParameter.class),
				bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				ValidationExceptions.VALIDATION_ERROR.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				ValidationExceptions.VALIDATION_ERROR.getErrorCode(),
				response.getBody().errorCode());

		assertEquals(
				"Validation failed.",
				response.getBody().errorMessage());

		assertEquals(
				1,
				response.getBody().validationErrors().size());
	}

	@Test
	void shouldHandleBindException() {

		BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");

		bindingResult.addError(
				new FieldError(
						"request",
						"name",
						"",
						false,
						null,
						null,
						"must not be blank"));

		BindException exception = new BindException(bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleBindException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				ValidationExceptions.VALIDATION_ERROR.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				ValidationExceptions.VALIDATION_ERROR.getErrorCode(),
				response.getBody().errorCode());

		assertEquals(
				"Validation failed.",
				response.getBody().errorMessage());

		assertEquals(
				1,
				response.getBody().validationErrors().size());
	}

	@Test
	void shouldHandleMissingServletRequestParameterException() {

		MissingServletRequestParameterException exception = new MissingServletRequestParameterException(
				"id",
				"String");

		ResponseEntity<ErrorResponse> response = handler.handleMissingServletRequestParameterException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorCode(),
				response.getBody().errorCode());
	}

	@Test
	void shouldHandleMethodArgumentTypeMismatchException() {

		MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
				"abc",
				Integer.class,
				"id",
				mock(MethodParameter.class),
				null);

		ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatchException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				"Invalid value 'abc' for parameter 'id'.",
				response.getBody().errorMessage());
	}

	@Test
	void shouldHandleHttpMessageNotReadableException() {

		HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
				"Malformed JSON",
				mock(HttpInputMessage.class));

		ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				"Malformed request body.",
				response.getBody().errorMessage());
	}

	@Test
	void shouldHandleIllegalArgumentException() {

		IllegalArgumentException exception = new IllegalArgumentException("Invalid request");

		ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		assertEquals(
				"Invalid request",
				response.getBody().errorMessage());
	}

	@Test
	void shouldHandleUnhandledException() {

		RuntimeException exception = new RuntimeException("Unexpected");

		ResponseEntity<ErrorResponse> response = handler.handleUnhandledException(
				exception);

		assertEquals(
				HttpStatus.INTERNAL_SERVER_ERROR,
				response.getStatusCode());

		assertEquals(
				WebExceptions.INTERNAL_SERVER_ERROR.getErrorName(),
				response.getBody().errorName());

		assertEquals(
				"Unexpected",
				response.getBody().errorMessage());
	}
}