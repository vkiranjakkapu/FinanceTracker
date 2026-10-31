package com.platform.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.platform.security.filter.AuthenticationContextInitializerFilter;
import com.platform.security.properties.SecurityProperties;

@AutoConfiguration(after = SecurityInfraAutoConfiguration.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityWebAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "platform.security", name = "enabled", havingValue = "true", matchIfMissing = true)
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			SecurityProperties properties,
			Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter,
			AuthenticationEntryPoint authenticationEntryPoint,
			AccessDeniedHandler accessDeniedHandler,
			AuthenticationContextInitializerFilter authenticationContextInitializerFilter) throws Exception {

		http.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(properties.getPublicPaths().toArray(String[]::new)).permitAll()
						.requestMatchers(properties.getAdminPaths().toArray(String[]::new)).hasRole("ADMIN")
						.anyRequest()
						.authenticated())
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(
								jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
				.addFilterAfter(
						authenticationContextInitializerFilter,
						BearerTokenAuthenticationFilter.class);

		return http.build();
	}

}
