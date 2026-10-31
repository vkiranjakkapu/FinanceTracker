package com.platform.security.exception;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class DefaultAccessDeniedHandlerTest {

    @Mock
    private ExceptionLogger exceptionLogger;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException accessDeniedException;

    private DefaultAccessDeniedHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DefaultAccessDeniedHandler(exceptionLogger);
    }

    @Test
    void shouldReturnForbidden() throws Exception {

        handler.handle(
                request,
                response,
                accessDeniedException);

        verify(exceptionLogger)
                .log(
                        accessDeniedException,
                        request,
                        HttpStatus.FORBIDDEN);

        verify(response)
                .sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden");

        verifyNoMoreInteractions(exceptionLogger, response);
    }
}
