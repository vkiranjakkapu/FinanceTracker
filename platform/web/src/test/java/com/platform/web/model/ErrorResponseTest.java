package com.platform.web.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.platform.web.exception.ValidationExceptions;
import com.platform.web.exception.WebExceptions;

class ErrorResponseTest {

	@Test
	void shouldCreateDefaultResponse() {

		ErrorResponse response = new ErrorResponse();

		assertEquals(
				WebExceptions.APPLICATION_ERROR.getErrorName(),
				response.errorName());

		assertEquals(
				WebExceptions.APPLICATION_ERROR.getErrorCode(),
				response.errorCode());

		assertEquals(
				WebExceptions.APPLICATION_ERROR.getErrorMessage(),
				response.errorMessage());

		assertNotNull(response.timestamp());
		assertNull(response.validationErrors());
	}

	@Test
	void shouldCreateResponseFromErrorCode() {

		ErrorResponse response = new ErrorResponse(WebExceptions.BAD_REQUEST);

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorName(),
				response.errorName());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorCode(),
				response.errorCode());

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorMessage(),
				response.errorMessage());

		assertNotNull(response.timestamp());
		assertNull(response.validationErrors());
	}

	@Test
	void shouldCreateResponseWithCustomMessage() {

		ErrorResponse response = new ErrorResponse(
				WebExceptions.BAD_REQUEST,
				"Custom message");

		assertEquals(
				WebExceptions.BAD_REQUEST.getErrorName(),
				response.errorName());

		assertEquals(
				"Custom message",
				response.errorMessage());

		assertNotNull(response.timestamp());
		assertNull(response.validationErrors());
	}

	@Test
	void shouldCreateResponseWithValidationErrors() {

		ValidationError validationError = new ValidationError(
				"email",
				"abc",
				"must be a valid email");

		ErrorResponse response = new ErrorResponse(
				ValidationExceptions.VALIDATION_ERROR,
				"Validation failed",
				List.of(validationError));

		assertEquals(
				ValidationExceptions.VALIDATION_ERROR.getErrorName(),
				response.errorName());

		assertEquals(
				"Validation failed",
				response.errorMessage());

		assertEquals(
				1,
				response.validationErrors().size());

		assertEquals(
				"email",
				response.validationErrors().getFirst().field());

		assertNotNull(response.timestamp());
	}

	@Test
	void shouldCreateResponseWithCustomValues() {

		ErrorResponse response = new ErrorResponse(
				"CUSTOM",
				"9999",
				"Custom error");

		assertEquals("CUSTOM", response.errorName());
		assertEquals("9999", response.errorCode());
		assertEquals("Custom error", response.errorMessage());

		assertNull(response.validationErrors());
		assertNotNull(response.timestamp());
	}
}