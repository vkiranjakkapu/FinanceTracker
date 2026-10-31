package com.platform.security.authentication;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class DefaultAuthenticationTokenProvider
        implements AuthenticationTokenProvider {

    @Override
    public Optional<String> getBearerToken() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken token)
                || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(token.getToken().getTokenValue());
    }

}