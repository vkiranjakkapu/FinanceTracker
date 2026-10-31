package com.platform.logging.aspect;

import static com.platform.logging.support.AspectTest.mockInvocation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;

class ExceptionLoggingAspectTest {

	private ProceedingJoinPoint joinPoint;

	private ExceptionLogger exceptionLogger;

	private ExceptionLoggingAspect aspect;

	private HttpServletRequest request;

	@BeforeEach
	void setUp() {

		joinPoint = mock(ProceedingJoinPoint.class);
		exceptionLogger = mock(ExceptionLogger.class);

		aspect = new ExceptionLoggingAspect(exceptionLogger);

		request = mock(HttpServletRequest.class);

		RequestContextHolder.setRequestAttributes(
				new ServletRequestAttributes(request));
	}

	@AfterEach
	void tearDown() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void shouldLogExceptionWhenHandlerReturnsResponseEntity() throws Throwable {

		Exception exception = new RuntimeException("boom");

		ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		mockInvocation(joinPoint, response, exception);

		Object result = aspect.logException(joinPoint);

		assertThat(result).isSameAs(response);

		verify(exceptionLogger).log(
				exception,
				request,
				HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldReturnOriginalResponse() throws Throwable {

		Exception exception = new RuntimeException();

		ResponseEntity<String> response = ResponseEntity.ok("success");

		mockInvocation(joinPoint, response, exception);

		Object result = aspect.logException(joinPoint);

		assertThat(result).isSameAs(response);
	}

	@Test
	void shouldNotLogWhenHandlerDoesNotReturnResponseEntity() throws Throwable {

		Exception exception = new RuntimeException();

		mockInvocation(joinPoint, "OK", exception);

		aspect.logException(joinPoint);

		verifyNoInteractions(exceptionLogger);
	}

	@Test
	void shouldNotLogWhenExceptionArgumentIsMissing() throws Throwable {

		ResponseEntity<Void> response = ResponseEntity.internalServerError().build();

		mockInvocation(joinPoint, response);

		aspect.logException(joinPoint);

		verifyNoInteractions(exceptionLogger);
	}

	@Test
	void shouldNotLogWhenCurrentRequestIsUnavailable() throws Throwable {

		RequestContextHolder.resetRequestAttributes();

		Exception exception = new RuntimeException();

		ResponseEntity<Void> response = ResponseEntity.internalServerError().build();

		mockInvocation(joinPoint, response, exception);

		aspect.logException(joinPoint);

		verifyNoInteractions(exceptionLogger);
	}
}