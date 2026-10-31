package com.platform.restclient.propagation;

import org.springframework.http.HttpHeaders;

public final class NoOpBearerTokenPropagator implements BearerTokenPropagator {

    @Override
    public void propagate(HttpHeaders headers) {
    }
}