package com.platform.restclient.propagation;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;

import com.platform.logging.holder.RequestContextHolder;
import com.platform.logging.properties.LoggingProperties;

public class DefaultHeaderContextPropagator
        implements HeaderContextPropagator {

    private final LoggingProperties properties;
    private final RequestContextHolder requestContextHolder;

    public DefaultHeaderContextPropagator(
            LoggingProperties properties,
            RequestContextHolder requestContextHolder) {
        this.properties = properties;
        this.requestContextHolder = requestContextHolder;
    }

    @Override
    public void propagate(HttpHeaders headers) {

        for (Map.Entry<String, String> mapping : properties.getHeaderMappings().entrySet()) {

            String headerName = mapping.getKey();
            String mdcKey = mapping.getValue();

            Optional<String> value = requestContextHolder.get(mdcKey);

            value.ifPresent(v -> headers.set(headerName, v));
        }
    }
}