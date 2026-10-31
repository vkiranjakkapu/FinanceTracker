package com.financetracker.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequestDto(
		@Email @NotBlank String email,
		@NotBlank String oldPassword,
		@NotBlank String newPassword) {
}
