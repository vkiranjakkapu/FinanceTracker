package com.platform.restclient.propagation;

import org.springframework.http.HttpHeaders;

import com.platform.security.authentication.AuthenticationTokenProvider;
import com.platform.security.properties.SecurityProperties;

public final class DefaultBearerTokenPropagator
        implements BearerTokenPropagator {

    private final AuthenticationTokenProvider authenticationTokenProvider;
    private final SecurityProperties securityProperties;

    public DefaultBearerTokenPropagator(
            AuthenticationTokenProvider authenticationTokenProvider,
            SecurityProperties securityProperties) {
        this.authenticationTokenProvider = authenticationTokenProvider;
        this.securityProperties = securityProperties;
    }

    @Override
    public void propagate(HttpHeaders headers) {

        authenticationTokenProvider.getBearerToken()
                .ifPresent(token -> headers.set(
                        securityProperties.getJwt().getHeaderName(),
                        securityProperties.getJwt().getTokenPrefix()+ " " + token));
    }
}
