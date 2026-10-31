package com.platform.logging.holder;

import java.util.Optional;

public interface RequestContextHolder {

    Optional<String> get(String key);

    void put(String key, String value);

    void remove(String key);

    void clear();
}