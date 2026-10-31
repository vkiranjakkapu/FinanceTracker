package com.financetracker.identity.services;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.financetracker.identity.dto.CreateUserRequestDto;
import com.financetracker.identity.dto.PasswordChangeRequestDto;
import com.financetracker.identity.dto.RegistrationRequest;
import com.financetracker.identity.dto.UpdateUserRequest;
import com.financetracker.identity.dto.UserResponse;
import com.financetracker.identity.entities.RoleType;

public interface UserService {
    UserResponse createUser(CreateUserRequestDto request);

    List<UserResponse> getAllUsers();

    List<UserResponse> getAllUsersByRole(RoleType role);

    List<UserResponse> getAllUsersWithIds(Collection<UUID> ids);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    UserResponse register(RegistrationRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    UserResponse changePassword(PasswordChangeRequestDto request);

    void deleteUser(UUID id);
}