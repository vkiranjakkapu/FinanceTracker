package com.financetracker.identity.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financetracker.identity.entities.Role;
import com.financetracker.identity.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findAllByRoles(Set<Role> roles);

    boolean existsByEmail(String email);

    List<User> findByIdIn(Collection<UUID> ids);

    List<User> findAllByRolesAndDeletedFalse(Set<Role> roles);

    List<User> findAllByDeletedFalse();

}