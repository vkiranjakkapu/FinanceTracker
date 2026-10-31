package com.platform.logging.support;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public final class MockHttpFactory {

    private MockHttpFactory() {
    }

    public static MockHttpServletRequest request() {
        return new MockHttpServletRequest();
    }

    public static MockHttpServletResponse response() {
        return new MockHttpServletResponse();
    }

    public static MockFilterChain filterChain() {
        return new MockFilterChain();
    }

    public static MockHttpServletRequest get(String uri) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(uri);

        return request;
    }
}