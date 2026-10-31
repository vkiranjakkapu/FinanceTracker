package com.platform.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WebExceptionsTest {

	@Test
	void shouldReturnInternalServerErrorProperties() {

		WebExceptions exception = WebExceptions.INTERNAL_SERVER_ERROR;

		assertEquals(
				"INTERNAL_SERVER_ERROR",
				exception.getErrorName());

		assertEquals(
				"WEB-5000",
				exception.getErrorCode());

		assertEquals(
				"An unexpected error occurred.",
				exception.getErrorMessage());
	}

	@Test
	void shouldReturnBadRequestProperties() {

		WebExceptions exception = WebExceptions.BAD_REQUEST;

		assertEquals(
				"BAD_REQUEST",
				exception.getErrorName());

		assertEquals(
				"WEB-4000",
				exception.getErrorCode());

		assertEquals(
				"Bad request.",
				exception.getErrorMessage());
	}

	@Test
	void shouldReturnResourceNotFoundProperties() {

		WebExceptions exception = WebExceptions.RESOURCE_NOT_FOUND;

		assertEquals(
				"RESOURCE_NOT_FOUND",
				exception.getErrorName());

		assertEquals(
				"WEB-4040",
				exception.getErrorCode());

		assertEquals(
				"Requested resource was not found.",
				exception.getErrorMessage());
	}
}