package com.platform.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfigurationSource;

import com.platform.logging.config.LoggingAutoConfiguration;
import com.platform.security.adapter.AuthenticationAdapter;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.filter.AuthenticationContextInitializerFilter;

public class SecurityAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(
							LoggingAutoConfiguration.class,
							SecurityInfraAutoConfiguration.class))
			.withPropertyValues(
					"platform.security.jwt.secret=test-secret-test-secret-test-secret");

	@Test
	void shouldCreateSecurityBeans() {

		contextRunner.run(context -> {
			assertThat(context)
					.hasSingleBean(AuthenticationContext.class);
			assertThat(context)
					.hasSingleBean(AuthenticationAdapter.class);
			assertThat(context)
					.hasSingleBean(JwtDecoder.class);
			assertThat(context)
					.hasSingleBean(
							AuthenticationContextInitializerFilter.class);
			assertThat(context)
					.hasSingleBean(
							AuthenticationEntryPoint.class);
			assertThat(context)
					.hasSingleBean(
							AccessDeniedHandler.class);
			assertThat(context)
					.hasSingleBean(
							Converter.class);
		});
	}

	@Test
	void shouldDisableSecurity() {

		contextRunner
				.withPropertyValues(
						"platform.security.enabled=false")
				.run(context -> {
					assertThat(context)
							.hasSingleBean(AuthenticationContext.class);
					assertThat(context)
							.hasSingleBean(AuthenticationAdapter.class);
					assertThat(context)
							.hasSingleBean(AuthenticationContextInitializerFilter.class);
				});
	}

	@Test
	void shouldNotCreateAuthenticationContextWhenCustomBeanExists() {

		contextRunner
				.withBean(
						AuthenticationContext.class,
						() -> mock(AuthenticationContext.class))
				.run(context -> {

					assertThat(context)
							.hasSingleBean(AuthenticationContext.class);
				});
	}

	@Test
	void shouldNotCreateJwtDecoderWhenJwtDisabled() {

		contextRunner
				.withPropertyValues(
						"platform.security.jwt.enabled=false")
				.run(context -> {
					assertThat(context)
							.doesNotHaveBean(JwtDecoder.class);
				});
	}

	@Test
	void shouldNotCreateCorsConfigurationSourceWhenCorsDisabled() {

		contextRunner
				.withPropertyValues(
						"platform.security.cors.enabled=false")
				.run(context -> {
					assertThat(context)
							.doesNotHaveBean(
									CorsConfigurationSource.class);
				});
	}
}
