package com.platform.restclient.interceptor;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.platform.restclient.propagation.BearerTokenPropagator;
import com.platform.restclient.propagation.HeaderContextPropagator;

public class RestClientInterceptor
        implements ClientHttpRequestInterceptor {

    private final HeaderContextPropagator headerContextPropagator;

    private final BearerTokenPropagator bearerTokenPropagator;

    public RestClientInterceptor(
            HeaderContextPropagator headerContextPropagator,
            BearerTokenPropagator bearerTokenPropagator) {

        this.headerContextPropagator = headerContextPropagator;
        this.bearerTokenPropagator = bearerTokenPropagator;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();

        headerContextPropagator.propagate(headers);
        bearerTokenPropagator.propagate(headers);

        return execution.execute(request, body);
    }
}