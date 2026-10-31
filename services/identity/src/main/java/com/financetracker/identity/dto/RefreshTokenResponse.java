package com.financetracker.identity.dto;

public record RefreshTokenResponse(
		String accessToken,
		String refreshToken) {
}
