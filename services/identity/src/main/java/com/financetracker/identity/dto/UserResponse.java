package com.financetracker.identity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.financetracker.identity.entities.Address;
import com.financetracker.identity.entities.RoleType;
import com.financetracker.identity.enums.UserGender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private UUID id;
	private String firstName;
	private String lastName;
	private String vehicleNumber;
	private String email;
	private String phone;
	private UserGender gender;
	private Address address;
	private LocalDate dob;
	private boolean enabled;
	private Set<RoleType> roles;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}