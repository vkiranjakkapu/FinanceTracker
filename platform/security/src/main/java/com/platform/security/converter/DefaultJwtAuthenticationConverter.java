package com.platform.security.converter;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.platform.security.properties.SecurityProperties;

public final class DefaultJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private SecurityProperties properties;

    public DefaultJwtAuthenticationConverter(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        List<String> roles = jwt.getClaimAsStringList(properties.getJwt().getAuthoritiesClaim());

        Collection<SimpleGrantedAuthority> authorities = roles == null
                ? List.of()
                : roles.stream()
                        .map(role -> new SimpleGrantedAuthority(properties.getJwt().getAuthorityPrefix() + role))
                        .toList();

        return new JwtAuthenticationToken(jwt, authorities);
    }
}