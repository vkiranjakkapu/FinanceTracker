package com.platform.logging.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;

import jakarta.servlet.http.HttpServletRequest;

public final class DefaultExceptionLogger implements ExceptionLogger {

	private static final Logger log = LoggerFactory.getLogger(DefaultExceptionLogger.class);

	@Override
	public void log(
			Exception exception,
			HttpServletRequest request,
			HttpStatusCode status) {

		log.error(
				"status={} method={} uri={} error={}",
				status.value(),
				request.getMethod(),
				request.getRequestURI(),
				exception.getMessage(),
				exception);
	}
}