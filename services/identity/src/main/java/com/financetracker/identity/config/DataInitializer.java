package com.financetracker.identity.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.financetracker.identity.entities.Address;
import com.financetracker.identity.entities.Role;
import com.financetracker.identity.entities.RoleType;
import com.financetracker.identity.entities.User;
import com.financetracker.identity.enums.UserGender;
import com.financetracker.identity.repository.RoleRepository;
import com.financetracker.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {

		Role adminRole = roleRepository.findByName(RoleType.ADMIN)
				.orElseGet(() -> roleRepository.save(
						new Role(null, RoleType.ADMIN, "Administrator")));
		roleRepository.findByName(RoleType.ADMIN)
				.orElseGet(() -> roleRepository.save(
						new Role(null, RoleType.ADMIN, "Admin")));
		roleRepository.findByName(RoleType.USER)
				.orElseGet(() -> roleRepository.save(
						new Role(null, RoleType.USER, "User")));

		if (userRepository.findByEmail("admin@pft.com").isEmpty()) {

			Address address = Address.builder()
					.id(null)
					.pinCode("534237")
					.street("street-1")
					.state("AP")
					.country("India")
					.build();
			User admin = User.builder()
					.firstName("System")
					.lastName("Admin")
					.email("admin@pft.com")
					.gender(UserGender.MALE)
					.password(passwordEncoder.encode("admin123"))
					.address(address)
					.enabled(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.roles(Set.of(adminRole)).build();

			userRepository.save(admin);
		}
	}

}