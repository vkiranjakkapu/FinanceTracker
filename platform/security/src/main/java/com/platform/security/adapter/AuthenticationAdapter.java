package com.platform.security.adapter;

import org.springframework.security.core.Authentication;

import com.platform.security.model.AuthenticatedUser;

public interface AuthenticationAdapter {

    AuthenticatedUser adapt(Authentication authentication);

}