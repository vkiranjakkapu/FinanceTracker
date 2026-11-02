package com.financetracker.identity.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.identity.dto.APIResponseDto;
import com.financetracker.identity.dto.CreateUserRequestDto;
import com.financetracker.identity.dto.FetchUsersRequestDto;
import com.financetracker.identity.dto.PasswordChangeRequestDto;
import com.financetracker.identity.dto.RegistrationRequest;
import com.financetracker.identity.dto.UpdateUserRequest;
import com.financetracker.identity.dto.UserResponse;
import com.financetracker.identity.entities.RoleType;
import com.financetracker.identity.services.UserService;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.web.model.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/identity/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AuthenticationContext authContext;

	@Operation(summary = "Get current user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Current user retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/me")
	public ResponseEntity<APIResponseDto<UserResponse>> me() {
		AuthenticatedUser user = authContext.getCurrentUser().orElse(null);
		return ResponseEntity
				.ok(APIResponseDto.<UserResponse>builder()
						.data(userService.getUserById(UUID.fromString(user.getUserId())))
						.build());
	}

	@Operation(summary = "Create user")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<APIResponseDto<UserResponse>> createUser(
			@Valid @RequestBody CreateUserRequestDto request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(APIResponseDto.<UserResponse>builder().data(userService.createUser(request)).build());
	}

	@Operation(summary = "Register user")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid registration details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/register")
	public ResponseEntity<APIResponseDto<UserResponse>> register(@Valid @ModelAttribute RegistrationRequest request) {
		return ResponseEntity.ok(APIResponseDto.<UserResponse>builder().data(userService.register(request)).build());
	}

	@Operation(summary = "Get all users")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<APIResponseDto<List<UserResponse>>> getAllUsers() {
		return ResponseEntity.ok(APIResponseDto.<List<UserResponse>>builder().data(userService.getAllUsers()).build());
	}

	@Operation(summary = "Get users by role")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid role", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/role/{role}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<APIResponseDto<List<UserResponse>>> getAllUsersByRole(@PathVariable RoleType type) {
		return ResponseEntity
				.ok(APIResponseDto.<List<UserResponse>>builder().data(userService.getAllUsersByRole(type)).build());
	}

	@Operation(summary = "Get user by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<APIResponseDto<UserResponse>> getUserById(@PathVariable UUID id) {
		return ResponseEntity.ok(APIResponseDto.<UserResponse>builder().data(userService.getUserById(id)).build());
	}

	@Operation(summary = "Search users by IDs")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user IDs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/search")
	public ResponseEntity<APIResponseDto<List<UserResponse>>> getAllUsersWithIds(
			@RequestBody FetchUsersRequestDto request) {
		return ResponseEntity
				.ok(APIResponseDto.<List<UserResponse>>builder().data(userService.getAllUsersWithIds(request.ids()))
						.build());
	}

	@Operation(summary = "Update user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PutMapping("/{id}")
	public ResponseEntity<APIResponseDto<UserResponse>> updateUser(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateUserRequest request) {

		return ResponseEntity
				.ok(APIResponseDto.<UserResponse>builder().data(userService.updateUser(id, request)).build());
	}

	@Operation(summary = "Change user password")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Password changed successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid password details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Authentication required or current password is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/")
	public ResponseEntity<APIResponseDto<UserResponse>> changePassword(
			@Valid @RequestBody PasswordChangeRequestDto request) {

		return ResponseEntity
				.ok(APIResponseDto.<UserResponse>builder().data(userService.changePassword(request)).build());
	}

	@Operation(summary = "Delete user")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User deleted successfully"),
			@ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<APIResponseDto<HashMap<String, Boolean>>> deleteUser(@PathVariable UUID id) {

		userService.deleteUser(id);
		HashMap<String, Boolean> body = new HashMap<>();
		body.put("status", true);

		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(APIResponseDto.<HashMap<String, Boolean>>builder().data(body).build());
	}
}
