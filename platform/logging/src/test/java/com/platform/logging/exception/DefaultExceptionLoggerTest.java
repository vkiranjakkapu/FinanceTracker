package com.platform.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import com.platform.logging.support.LogCapture;

class DefaultExceptionLoggerTest {

    private DefaultExceptionLogger exceptionLogger;
    private LogCapture logs;

    @BeforeEach
    void setUp() {

        exceptionLogger = new DefaultExceptionLogger();

        logs = LogCapture.attach(DefaultExceptionLogger.class);
    }

    @AfterEach
    void tearDown() {
        logs.close();
    }

    @Test
    void shouldLogException() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI("/users");

        Exception exception = new IllegalArgumentException("Invalid request");

        exceptionLogger.log(
                exception,
                request,
                HttpStatus.BAD_REQUEST);

        assertThat(logs.size())
                .isEqualTo(1);

        assertThat(logs.message(0))
                .contains("status=400")
                .contains("method=GET")
                .contains("uri=/users")
                .contains("error=Invalid request");
    }

}