package com.platform.security.model;

import java.util.Collection;

public interface AuthenticatedUser {

    String getUserId();

    String getUsername();

    String getEmail();

    Collection<String> getAuthorities();

}