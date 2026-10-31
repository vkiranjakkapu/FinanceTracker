package com.financetracker.identity.dto;

import java.time.LocalDate;

import com.financetracker.identity.enums.UserGender;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
		@NotNull String firstName,
		@NotNull String lastName,
		String vehicleNumber,
		@NotNull String phone,
		@NotNull UserGender gender,
		@NotNull LocalDate dob,
		AddressDto address,
		Boolean enabled) {
}
