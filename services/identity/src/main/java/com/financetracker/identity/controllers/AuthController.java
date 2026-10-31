package com.financetracker.identity.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.identity.dto.APIResponseDto;
import com.financetracker.identity.dto.LoginRequestDto;
import com.financetracker.identity.dto.LoginResponseDto;
import com.financetracker.identity.dto.LogoutRequestDto;
import com.financetracker.identity.dto.RefreshTokenRequest;
import com.financetracker.identity.dto.RefreshTokenResponse;
import com.financetracker.identity.services.AuthenticationService;
import com.platform.web.model.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/identity/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/")
    public ResponseEntity<APIResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity
                .ok(APIResponseDto.<LoginResponseDto>builder().data(authenticationService.login(request)).build());
    }

    @Operation(summary = "Refresh access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<APIResponseDto<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(
                APIResponseDto.<RefreshTokenResponse>builder().data(authenticationService.refresh(request)).build());
    }

    @Operation(summary = "Logout user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        authenticationService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
