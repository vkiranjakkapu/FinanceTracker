package com.financetracker.identity.services;

import com.financetracker.identity.dto.LoginRequestDto;
import com.financetracker.identity.dto.LoginResponseDto;
import com.financetracker.identity.dto.LogoutRequestDto;
import com.financetracker.identity.dto.RefreshTokenRequest;
import com.financetracker.identity.dto.RefreshTokenResponse;

public interface AuthenticationService {

    LoginResponseDto login(LoginRequestDto request);

    RefreshTokenResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequestDto request);

}