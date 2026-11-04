package com.financetracker.finance.services.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.services.CurrentUserService;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.web.exception.SecurityExceptions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImp implements CurrentUserService {

    private final AuthenticationContext authenticationContext;

    @Override
    public AuthenticatedUser currentUser() {
        return authenticationContext.getCurrentUser()
                .orElseThrow(() -> new BusinessException(
                        SecurityExceptions.FORBIDDEN_ACCESS,
                        "No authenticated user",
                        HttpStatus.FORBIDDEN));
    }

    @Override
    public UUID userId() {
        return UUID.fromString(currentUser().getUserId());
    }

    @Override
    public String username() {
        return currentUser().getUsername();
    }

    @Override
    public String email() {
        return currentUser().getEmail();
    }

    @Override
    public boolean isAdmin() {
        return currentUser().getAuthorities().contains("ROLE_ADMIN");
    }

    @Override
    public boolean isRider() {
        return currentUser().getAuthorities().contains("ROLE_RIDER");
    }

    @Override
    public boolean isDriver() {
        return currentUser().getAuthorities().contains("ROLE_DRIVER");
    }

}
