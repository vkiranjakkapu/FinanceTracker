package com.platform.security.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.platform.logging.exception.ExceptionLogger;
import com.platform.logging.holder.RequestContextHolder;
import com.platform.security.adapter.AuthenticatedUserAdapter;
import com.platform.security.adapter.AuthenticationAdapter;
import com.platform.security.authentication.AuthenticationTokenProvider;
import com.platform.security.authentication.DefaultAuthenticationTokenProvider;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.context.DefaultAuthenticationContext;
import com.platform.security.converter.DefaultJwtAuthenticationConverter;
import com.platform.security.exception.DefaultAccessDeniedHandler;
import com.platform.security.exception.DefaultAuthenticationEntryPoint;
import com.platform.security.filter.AuthenticationContextInitializerFilter;
import com.platform.security.properties.SecurityProperties;
import com.platform.security.properties.SecurityProperties.CorsProperties;

@AutoConfiguration
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityInfraAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "platform.security.jwt", name = "enabled", havingValue = "true", matchIfMissing = true)
	public JwtDecoder jwtDecoder(SecurityProperties properties) {

		SecretKey secretKey = new SecretKeySpec(
				properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8),
				"HmacSHA256");

		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey).build();

		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
				properties.getJwt().getIssuer());

		OAuth2TokenValidator<Jwt> withClockSkew = new JwtTimestampValidator(
				properties.getJwt().getClockSkew());

		decoder.setJwtValidator(
				new DelegatingOAuth2TokenValidator<>(
						withIssuer,
						withClockSkew));
		return decoder;
	}

	@Bean
	@ConditionalOnMissingBean
	public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(SecurityProperties properties) {
		return new DefaultJwtAuthenticationConverter(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationEntryPoint authenticationEntryPoint(
			ExceptionLogger exceptionLogger) {

		return new DefaultAuthenticationEntryPoint(exceptionLogger);
	}

	@Bean
	@ConditionalOnMissingBean
	public AccessDeniedHandler accessDeniedHandler(
			ExceptionLogger exceptionLogger) {

		return new DefaultAccessDeniedHandler(exceptionLogger);
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationContextInitializerFilter authenticationContextInitializerFilter(
			AuthenticationContext authenticationContext,
			RequestContextHolder requestContextHolder) {

		return new AuthenticationContextInitializerFilter(
				authenticationContext,
				requestContextHolder);
	}

	@Bean
	@ConditionalOnProperty(prefix = "platform.security.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
	public CorsConfigurationSource corsConfigurationSource(
			SecurityProperties properties) {
		CorsProperties cors = properties.getCors();

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOriginPatterns(cors.getAllowedOriginPatterns());
		configuration.setAllowedMethods(cors.getAllowedMethods());
		configuration.setAllowedHeaders(cors.getAllowedHeaders());
		configuration.setExposedHeaders(cors.getExposedHeaders());
		configuration.setAllowCredentials(cors.isAllowCredentials());
		configuration.setMaxAge(cors.getMaxAge());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationAdapter authenticationAdapter(SecurityProperties properties) {
		return new AuthenticatedUserAdapter(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationContext authenticationContext(
			AuthenticationAdapter adapter) {

		return new DefaultAuthenticationContext(adapter);
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationTokenProvider authenticationTokenProvider() {
		return new DefaultAuthenticationTokenProvider();
	}
}
