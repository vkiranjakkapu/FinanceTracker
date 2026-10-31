package com.platform.security.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import com.platform.security.properties.SecurityProperties;

@ExtendWith(MockitoExtension.class)
class DefaultJwtAuthenticationConverterTest {

    private SecurityProperties properties;

    private DefaultJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {

        properties = new SecurityProperties();

        converter = new DefaultJwtAuthenticationConverter(properties);
    }

    @Test
    void shouldConvertJwtToAuthenticationToken() {

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("sub", "1234567890")
                .claim("username", "venkat")
                .claim("roles", List.of("USER", "ADMIN"))
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertThat(authentication).isNotNull();

        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(
                        "ROLE_USER",
                        "ROLE_ADMIN");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRolesClaimIsMissing() {

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("sub", "1234567890")
                .claim("username", "venkat")
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertThat(authentication.getAuthorities())
                .isEmpty();
    }

    @Test
    void shouldUseConfiguredAuthorityPrefix() {

        properties.getJwt().setAuthorityPrefix("SCOPE_");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("roles", List.of("USER"))
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("SCOPE_USER");
    }

    @Test
    void shouldUseConfiguredRolesClaim() {

        properties.getJwt().setAuthoritiesClaim("permissions");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("permissions", List.of("READ", "WRITE"))
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(
                        "ROLE_READ",
                        "ROLE_WRITE");
    }
}