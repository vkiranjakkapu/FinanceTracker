package com.platform.logging.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.platform.logging.properties.LoggingProperties;
import com.platform.logging.support.LogCapture;
import com.platform.logging.support.MockHttpFactory;

class RequestLoggingFilterTest {

    private RequestLoggingFilter filter;
    private LoggingProperties properties;

    private LogCapture logs;

    @BeforeEach
    void setUp() {

        properties = new LoggingProperties();
        filter = new RequestLoggingFilter(properties);

        logs = LogCapture.attach(RequestLoggingFilter.class);
    }

    @AfterEach
    void tearDown() {
        logs.close();
    }

    @Test
    void shouldLogIncomingRequest() throws Exception {

        // Given
        MockHttpServletRequest request = MockHttpFactory.get("/users");

        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(logs.size()).isEqualTo(2);

        assertThat(logs.message(0))
                .contains("REQ GET /users");
    }

    @Test
    void shouldLogOutgoingResponse() throws Exception {

        // Given
        MockHttpServletRequest request = MockHttpFactory.get("/users");

        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(logs.message(1))
                .contains("RES GET /users")
                .contains("status=200");
    }

    @Test
    void shouldIncludeQueryStringWhenEnabled() throws Exception {

        // Given
        properties.getRequest().setIncludeQueryString(true);

        MockHttpServletRequest request = MockHttpFactory.get("/users");
        request.setQueryString("page=1");

        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(logs.message(0))
                .contains("/users?page=1");
    }

    @Test
    void shouldNotIncludeQueryStringWhenDisabled() throws Exception {

        // Given
        properties.getRequest().setIncludeQueryString(false);

        MockHttpServletRequest request = MockHttpFactory.get("/users");
        request.setQueryString("page=1");

        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(logs.message(0))
                .doesNotContain("page=1");
    }

    @Test
    void shouldIncludeClientIpWhenEnabled() throws Exception {

        // Given
        properties.getRequest().setIncludeClientIp(true);

        MockHttpServletRequest request = MockHttpFactory.get("/users");

        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(logs.message(0))
                .contains("client=127.0.0.1");
    }

    @Test
    void shouldContinueFilterChain() throws Exception {

        // Given
        MockHttpServletRequest request = MockHttpFactory.request();
        MockHttpServletResponse response = MockHttpFactory.response();
        MockFilterChain filterChain = MockHttpFactory.filterChain();

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        assertThat(filterChain.getRequest()).isNotNull();
        assertThat(filterChain.getResponse()).isNotNull();
    }
}