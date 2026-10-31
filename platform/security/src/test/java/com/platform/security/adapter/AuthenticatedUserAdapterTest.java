package com.platform.security.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.platform.security.model.AuthenticatedUser;
import com.platform.security.properties.SecurityProperties;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserAdapterTest {

    private SecurityProperties properties;

    private AuthenticatedUserAdapter adapter;

    @BeforeEach
    void setUp() {

        properties = new SecurityProperties();

        adapter = new AuthenticatedUserAdapter(properties);
    }

    @Test
    void shouldAdaptJwtAuthenticationToken() {

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("sub", "1234567890")
                .claim("username", "venkat")
                .claim("roles", List.of("USER"))
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                jwt,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        AuthenticatedUser user = adapter.adapt(authentication);

        assertThat(user.getUserId())
                .isEqualTo("1234567890");

        assertThat(user.getUsername())
                .isEqualTo("venkat");

        assertThat(user.getAuthorities())
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldHandleMissingAuthorities() {

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("sub", "1")
                .claim("username", "venkat")
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

        AuthenticatedUser user = adapter.adapt(authentication);

        assertThat(user.getAuthorities())
                .isEmpty();
    }

    @Test
    void shouldRejectUnsupportedAuthenticationType() {

        Authentication authentication = mock(Authentication.class);

        assertThatThrownBy(() -> adapter.adapt(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported authentication type");
    }
}