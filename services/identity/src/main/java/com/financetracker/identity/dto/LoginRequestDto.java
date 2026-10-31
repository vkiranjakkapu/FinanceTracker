package com.financetracker.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
		@Email @NotNull String email,
		@NotNull String password) {
}