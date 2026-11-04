package com.financetracker.finance.services;

import java.util.UUID;

import com.platform.security.model.AuthenticatedUser;

public interface CurrentUserService {

    AuthenticatedUser currentUser();

    UUID userId();

    String username();

    String email();

    boolean isAdmin();

    boolean isRider();

    boolean isDriver();

}