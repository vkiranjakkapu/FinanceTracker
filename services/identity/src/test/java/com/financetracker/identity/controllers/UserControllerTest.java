package com.financetracker.identity.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.financetracker.identity.dto.AddressDto;
import com.financetracker.identity.dto.CreateUserRequestDto;
import com.financetracker.identity.dto.FetchUsersRequestDto;
import com.financetracker.identity.dto.UpdateUserRequest;
import com.financetracker.identity.dto.UserResponse;
import com.financetracker.identity.entities.RoleType;
import com.financetracker.identity.enums.UserGender;
import com.financetracker.identity.services.UserService;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.security.model.DefaultAuthenticatedUser;

import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JsonMapper jsonMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthenticationContext authenticationContext;

	@BeforeEach
	void setup() {

		AuthenticatedUser authenticatedUser = new DefaultAuthenticatedUser(
				UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3").toString(),
				"john@test.com",
				"admin",
				List.of("ROLE_ADMIN"));

		when(authenticationContext.getCurrentUser())
				.thenReturn(Optional.of(authenticatedUser));
	}

	@Test
	void me_ShouldReturn200() throws Exception {

		UUID id = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

		when(userService.getUserById(id))
				.thenReturn(response());

		mockMvc.perform(get("/identity/api/v1/users/me")
				.with(adminJwt()))
				.andExpect(status().isOk());
	}

	@Test
	void createUser_ShouldReturn201() throws Exception {

		when(userService.createUser(any()))
				.thenReturn(response());

		mockMvc.perform(post("/identity/api/v1/users/")
				.with(adminJwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(createRequest())))
				.andExpect(status().isCreated());
	}

	@Test
	void getAllUsers_ShouldReturn200() throws Exception {

		when(userService.getAllUsers())
				.thenReturn(List.of(response()));

		mockMvc.perform(get("/identity/api/v1/users/")
				.with(adminJwt()))
				.andExpect(status().isOk());
	}

	@Test
	void getUserById_ShouldReturn200() throws Exception {

		when(userService.getUserById(UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3")))
				.thenReturn(response());

		mockMvc.perform(get("/identity/api/v1/users/" + UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3"))
				.with(adminJwt()))
				.andExpect(status().isOk());
	}

	@Test
	void updateUser_ShouldReturn200() throws Exception {
		UUID id = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

		when(userService.updateUser(eq(id), any(UpdateUserRequest.class)))
				.thenReturn(response());

		mockMvc.perform(put("/identity/api/v1/users/" + id)
				.with(adminJwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(updateRequest())))
				.andExpect(status().isOk());
	}

	@Test
	void deleteUser_ShouldReturn204() throws Exception {

		doNothing().when(userService).deleteUser(UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3"));

		mockMvc.perform(delete("/identity/api/v1/users/" + UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3"))
				.with(adminJwt()))
				.andExpect(status().isNoContent());
	}

	@Test
	void createUser_InvalidRequest_ShouldReturn400() throws Exception {

		CreateUserRequestDto request = new CreateUserRequestDto(
				"invalid-email",
				"",
				"",
				UserGender.MALE,
				"",
				null,
				"",
				null,
				null);

		mockMvc.perform(post("/identity/api/v1/users/")
				.with(adminJwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getAllUsersWithIds_ShouldReturn200() throws Exception {
		UUID id = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

		FetchUsersRequestDto request = new FetchUsersRequestDto(List.of(id));

		when(userService.getAllUsersWithIds(List.of(id)))
				.thenReturn(List.of(response()));

		mockMvc.perform(post("/identity/api/v1/users/search")
				.with(adminJwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void updateUser_InvalidRequest_ShouldReturn400() throws Exception {

		UpdateUserRequest request = new UpdateUserRequest(
				"",
				"",
				"",
				"",
				UserGender.NON_DISCLOSED,
				null,
				null,
				true);

		mockMvc.perform(put("/identity/api/v1/users/" +
				UUID.fromString("c0186249-1111-4927-97b3-a08a21febfe3"))
				.with(adminJwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	private CreateUserRequestDto createRequest() {

		return new CreateUserRequestDto(
				"john@test.com",
				"John",
				"Doe",
				UserGender.MALE,
				"password",
				LocalDate.of(2000, 1, 1),
				"9999999999",
				new AddressDto(
						"Street",
						"534237",
						"AP",
						"India"),
				RoleType.ADMIN);
	}

	private UpdateUserRequest updateRequest() {
		return new UpdateUserRequest(
				"John",
				"Doe",
				"AB01CD2345",
				"8888888888",
				UserGender.NON_DISCLOSED,
				LocalDate.of(2001, 3, 1),
				AddressDto.builder()
						.street("New Street")
						.pinCode("534237")
						.state("AP")
						.country("India")
						.build(),
				true);
	}

	private UserResponse response() {

		return new UserResponse(
				UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3"),
				"john@test.com",
				"John",
				"",
				"Doe",
				"9999999999",
				UserGender.NON_DISCLOSED,
				null,
				LocalDate.of(2000, 1, 1),
				true,
				Set.of(RoleType.ADMIN),
				LocalDateTime.now(),
				LocalDateTime.now());
	}

	private JwtRequestPostProcessor adminJwt() {

		return jwt()
				.jwt(jwt -> jwt
						.subject(UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3").toString())
						.claim("username", "admin")
						.claim("email", "admin@email.com"))
				.authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
}