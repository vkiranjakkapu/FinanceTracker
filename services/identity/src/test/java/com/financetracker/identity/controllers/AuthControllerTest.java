package com.financetracker.identity.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.financetracker.identity.dto.LoginRequestDto;
import com.financetracker.identity.dto.LoginResponseDto;
import com.financetracker.identity.dto.LogoutRequestDto;
import com.financetracker.identity.dto.RefreshTokenRequest;
import com.financetracker.identity.dto.RefreshTokenResponse;
import com.financetracker.identity.services.AuthenticationService;

import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void login_ShouldReturn200() throws Exception {

        LoginRequestDto request = new LoginRequestDto(
                "admin@test.com",
                "password");

        LoginResponseDto response = new LoginResponseDto(
                "access-token",
                "refresh-token",
                "Bearer");

        when(authenticationService.login(any()))
                .thenReturn(response);

        mockMvc.perform(post("/identity/api/v1/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void refresh_ShouldReturn200() throws Exception {

        RefreshTokenRequest request = new RefreshTokenRequest(
                "refresh-token");

        RefreshTokenResponse response = new RefreshTokenResponse(
                "new-access-token",
                "refresh-token");

        when(authenticationService.refresh(any()))
                .thenReturn(response);

        mockMvc.perform(post("/identity/api/v1/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void logout_ShouldReturn204() throws Exception {

        LogoutRequestDto request = new LogoutRequestDto("refresh-token");

        doNothing().when(authenticationService)
                .logout(any());

        mockMvc.perform(post("/identity/api/v1/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}