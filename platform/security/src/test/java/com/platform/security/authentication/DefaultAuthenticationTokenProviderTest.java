package com.platform.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class DefaultAuthenticationTokenProviderTest {

	private final DefaultAuthenticationTokenProvider tokenProvider = new DefaultAuthenticationTokenProvider();

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldReturnBearerTokenForAuthenticatedJwt() {

		Jwt jwt = Jwt.withTokenValue("test-token")
				.header("alg", "none")
				.claim("sub", "user-1")
				.build();

		JwtAuthenticationToken authentication = new JwtAuthenticationToken(
				jwt,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertThat(tokenProvider.getBearerToken())
				.contains("test-token");
	}

	@Test
	void shouldReturnEmptyWhenAuthenticationIsNull() {

		SecurityContextHolder.clearContext();

		assertThat(tokenProvider.getBearerToken())
				.isEmpty();
	}

	@Test
	void shouldReturnEmptyWhenAuthenticationIsUnsupported() {

		TestingAuthenticationToken authentication = new TestingAuthenticationToken("user", "password");

		authentication.setAuthenticated(true);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertThat(tokenProvider.getBearerToken())
				.isEmpty();
	}

	@Test
	void shouldReturnEmptyWhenJwtAuthenticationIsNotAuthenticated() {

		Jwt jwt = Jwt.withTokenValue("test-token")
				.header("alg", "none")
				.claim("sub", "user-1")
				.build();

		JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

		authentication.setAuthenticated(false);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertThat(tokenProvider.getBearerToken())
				.isEmpty();
	}

}
