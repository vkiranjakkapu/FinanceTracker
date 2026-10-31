package com.platform.logging.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.platform.logging.constants.LoggingConstants;
import com.platform.logging.generator.CorrelationIdGenerator;
import com.platform.logging.holder.DefaultRequestContextHolder;
import com.platform.logging.holder.RequestContextHolder;
import com.platform.logging.properties.LoggingProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class CorrelationIdFilterTest {

	private CorrelationIdFilter filter;
	private LoggingProperties properties;
	private CorrelationIdGenerator correlationIdGenerator;
	private RequestContextHolder requestContextHolder;

	@BeforeEach
	void setUp() {

		properties = new LoggingProperties();

		correlationIdGenerator = mock(CorrelationIdGenerator.class);
		requestContextHolder = new DefaultRequestContextHolder();

		filter = new CorrelationIdFilter(
				properties,
				correlationIdGenerator,
				requestContextHolder);
	}

	@AfterEach
	void tearDown() {
		requestContextHolder.clear();
	}

	@Test
	void shouldGenerateCorrelationIdWhenHeaderMissing() throws Exception {

		// Given
		MockHttpServletRequest request = request();
		MockHttpServletResponse response = response();
		MockFilterChain filterChain = filterChain();

		// When
		filter.doFilter(request, response, filterChain);

		// Then
		verify(correlationIdGenerator)
				.generate(request);
	}

	@Test
	void shouldReuseExistingCorrelationId() throws Exception {

		// Given
		String existingCorrelationId = "existing-correlation-id";

		MockHttpServletRequest request = request();
		request.addHeader(
				LoggingConstants.CORRELATION_HEADER,
				existingCorrelationId);

		MockHttpServletResponse response = response();
		MockFilterChain filterChain = filterChain();

		// When
		filter.doFilter(request, response, filterChain);

		// Then
		assertThat(
				response.getHeader(LoggingConstants.CORRELATION_HEADER))
				.isEqualTo(existingCorrelationId);

		verifyNoInteractions(correlationIdGenerator);
	}

	@Test
	void shouldPopulateAndClearMdc() throws Exception {

		// Given
		String correlationId = "test-correlation-id";

		when(correlationIdGenerator.generate(any()))
				.thenReturn(correlationId);

		MockHttpServletRequest request = request();
		MockHttpServletResponse response = response();

		FilterChain filterChain = mock(FilterChain.class);

		doAnswer(invocation -> {

			assertThat(requestContextHolder
					.get(LoggingConstants.CORRELATION_ID)
					.orElse(null))
					.isEqualTo(correlationId);

			return null;

		}).when(filterChain).doFilter(any(), any());

		// When
		filter.doFilter(request, response, filterChain);

		// Then
		assertThat(requestContextHolder
				.get(LoggingConstants.CORRELATION_ID)
				.orElse(null)).isNull();
	}

	@Test
	void shouldWriteCorrelationIdToResponseHeader() throws Exception {

		// Given
		String correlationId = "test-correlation-id";

		when(correlationIdGenerator.generate(any()))
				.thenReturn(correlationId);

		MockHttpServletRequest request = request();
		MockHttpServletResponse response = response();
		MockFilterChain filterChain = filterChain();

		// When
		filter.doFilter(request, response, filterChain);

		// Then
		assertThat(response.getHeader(LoggingConstants.CORRELATION_HEADER))
				.isEqualTo(correlationId);
	}

	@Test
	void shouldContinueFilterChain() throws Exception {

		// Given
		MockHttpServletRequest request = request();
		MockHttpServletResponse response = response();

		FilterChain filterChain = mock(FilterChain.class);

		// When
		filter.doFilter(request, response, filterChain);

		// Then
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldClearCorrelationIdWhenFilterChainThrowsException()
			throws Exception {

		// Given
		when(correlationIdGenerator.generate(any()))
				.thenReturn("test-id");

		MockHttpServletRequest request = request();
		MockHttpServletResponse response = response();

		FilterChain filterChain = mock(FilterChain.class);

		doThrow(new ServletException("boom"))
				.when(filterChain)
				.doFilter(any(), any());

		// When
		assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
				.isInstanceOf(ServletException.class);

		// Then
		assertThat(requestContextHolder.get(LoggingConstants.CORRELATION_ID)).isEmpty();
	}

	private MockHttpServletRequest request() {
		return new MockHttpServletRequest();
	}

	private MockHttpServletResponse response() {
		return new MockHttpServletResponse();
	}

	private MockFilterChain filterChain() {
		return new MockFilterChain();
	}
}