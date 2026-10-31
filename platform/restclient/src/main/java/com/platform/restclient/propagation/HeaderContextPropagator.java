package com.platform.restclient.propagation;

import org.springframework.http.HttpHeaders;

public interface HeaderContextPropagator {

    void propagate(HttpHeaders headers);

}