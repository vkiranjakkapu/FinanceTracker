package com.platform.security.context;

import java.util.Optional;

import com.platform.security.model.AuthenticatedUser;

public interface AuthenticationContext {

    Optional<AuthenticatedUser> getCurrentUser();

    boolean isAuthenticated();
}