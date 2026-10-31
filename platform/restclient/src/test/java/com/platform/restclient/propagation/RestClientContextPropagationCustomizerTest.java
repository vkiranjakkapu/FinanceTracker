package com.platform.restclient.propagation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.platform.restclient.customizer.DefaultRestClientCustomizer;
import com.platform.restclient.interceptor.RestClientInterceptor;

class RestClientContextPropagationCustomizerTest {

    private RestClientInterceptor interceptor;
    private DefaultRestClientCustomizer customizer;

    @BeforeEach
    void setUp() {

        interceptor = mock(RestClientInterceptor.class);

        customizer = new DefaultRestClientCustomizer(interceptor);
    }

    @Test
    void shouldRegisterHeaderContextPropagationInterceptor() {

        RestClient.Builder builder = mock(RestClient.Builder.class);

        when(builder.requestInterceptor(interceptor))
                .thenReturn(builder);

        customizer.customize(builder);

        verify(builder).requestInterceptor(interceptor);
        verifyNoMoreInteractions(builder);
    }
}