package com.platform.security.model;

import java.util.Collection;
import java.util.List;

public final class DefaultAuthenticatedUser implements AuthenticatedUser {

    private final String userId;
    private final String username;
    private final String email;
    private final Collection<String> authorities;

    public DefaultAuthenticatedUser(
            String userId,
            String username,
            String email,
            Collection<String> authorities) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.authorities = List.copyOf(authorities);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Collection<String> getAuthorities() {
        return authorities;
    }
}
