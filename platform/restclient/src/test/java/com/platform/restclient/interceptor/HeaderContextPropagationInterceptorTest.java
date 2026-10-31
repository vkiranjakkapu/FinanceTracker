package com.platform.restclient.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import com.platform.restclient.propagation.BearerTokenPropagator;
import com.platform.restclient.propagation.HeaderContextPropagator;

class HeaderContextPropagationInterceptorTest {

    private HeaderContextPropagator headerContextPropagator;
    private BearerTokenPropagator bearerTokenPropagator;
    private RestClientInterceptor interceptor;

    @BeforeEach
    void setUp() {

        headerContextPropagator = mock(HeaderContextPropagator.class);
        bearerTokenPropagator = mock(BearerTokenPropagator.class);

        interceptor = new RestClientInterceptor(
                headerContextPropagator, bearerTokenPropagator);
    }

    @Test
    void shouldPropagateContext() throws IOException {

        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders headers = new HttpHeaders();

        when(request.getHeaders()).thenReturn(headers);

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

        ClientHttpResponse response = mock(ClientHttpResponse.class);

        when(execution.execute(any(), any()))
                .thenReturn(response);

        interceptor.intercept(request, new byte[0], execution);

        verify(headerContextPropagator).propagate(headers);
    }

    @Test
    void shouldContinueRequestExecution() throws IOException {

        HttpRequest request = mock(HttpRequest.class);

        when(request.getHeaders())
                .thenReturn(new HttpHeaders());

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

        ClientHttpResponse response = mock(ClientHttpResponse.class);

        when(execution.execute(any(), any()))
                .thenReturn(response);

        byte[] body = new byte[0];

        // Only one invocation
        interceptor.intercept(request, body, execution);

        verify(execution).execute(request, body);
    }

    @Test
    void shouldReturnResponseFromExecution() throws IOException {

        HttpRequest request = mock(HttpRequest.class);

        when(request.getHeaders())
                .thenReturn(new HttpHeaders());

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

        ClientHttpResponse expected = mock(ClientHttpResponse.class);

        when(execution.execute(any(), any()))
                .thenReturn(expected);

        ClientHttpResponse actual = interceptor.intercept(
                request,
                new byte[0],
                execution);

        assertThat(actual).isSameAs(expected);
    }
}