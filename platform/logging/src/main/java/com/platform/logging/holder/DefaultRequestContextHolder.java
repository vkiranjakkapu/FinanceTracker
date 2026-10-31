package com.platform.logging.holder;

import org.slf4j.MDC;

import java.util.Optional;

public class DefaultRequestContextHolder
        implements RequestContextHolder {

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(MDC.get(key));
    }

    @Override
    public void put(String key, String value) {
        MDC.put(key, value);
    }

    @Override
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override
    public void clear() {
        MDC.clear();
    }
}