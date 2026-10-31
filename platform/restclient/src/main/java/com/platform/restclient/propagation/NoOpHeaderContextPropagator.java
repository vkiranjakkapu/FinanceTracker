package com.platform.restclient.propagation;

import org.springframework.http.HttpHeaders;

public class NoOpHeaderContextPropagator implements HeaderContextPropagator {

    @Override
    public void propagate(HttpHeaders headers) {
    }

}
