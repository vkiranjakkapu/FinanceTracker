package com.platform.restclient.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.platform.logging.holder.RequestContextHolder;
import com.platform.logging.properties.LoggingProperties;
import com.platform.restclient.interceptor.RestClientInterceptor;
import com.platform.restclient.propagation.BearerTokenPropagator;
import com.platform.restclient.propagation.DefaultBearerTokenPropagator;
import com.platform.restclient.propagation.DefaultHeaderContextPropagator;
import com.platform.restclient.propagation.HeaderContextPropagator;
import com.platform.restclient.propagation.NoOpBearerTokenPropagator;
import com.platform.restclient.propagation.NoOpHeaderContextPropagator;
import com.platform.security.authentication.AuthenticationTokenProvider;
import com.platform.security.properties.SecurityProperties;

class RestClientAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(RestClientAutoConfiguration.class))
			.withBean(
					RequestContextHolder.class,
					() -> mock(RequestContextHolder.class))
			.withBean(
					LoggingProperties.class,
					LoggingProperties::new)
			.withBean(
					SecurityProperties.class,
					SecurityProperties::new);

	@Test
	void shouldCreateDefaultBeans() {

		contextRunner.run(context -> {

			assertThat(context).hasSingleBean(RestClientCustomizer.class);
			assertThat(context).hasSingleBean(RestClientInterceptor.class);

			assertThat(context)
					.getBean(HeaderContextPropagator.class)
					.isInstanceOf(DefaultHeaderContextPropagator.class);

			assertThat(context)
					.getBean(BearerTokenPropagator.class)
					.isInstanceOf(NoOpBearerTokenPropagator.class);
		});
	}

	@Test
	void shouldUseNoOpHeaderContextPropagatorWhenDisabled() {

		contextRunner
				.withPropertyValues(
						"platform.rest-client.propagation.context.enabled=false")
				.run(context -> {

					assertThat(context)
							.getBean(HeaderContextPropagator.class)
							.isInstanceOf(NoOpHeaderContextPropagator.class);
				});
	}

	@Test
	void shouldUseNoOpBearerTokenPropagatorWhenDisabled() {

		contextRunner
				.withPropertyValues(
						"platform.rest-client.propagation.bearer-token.enabled=false")
				.run(context -> {

					assertThat(context)
							.getBean(BearerTokenPropagator.class)
							.isInstanceOf(NoOpBearerTokenPropagator.class);
				});
	}

	@Test
	void shouldCreateDefaultBearerTokenPropagatorWhenEnabled() {

		contextRunner
				.withPropertyValues(
						"platform.rest-client.propagation.bearer-token.enabled=true")
				.withBean(
						AuthenticationTokenProvider.class,
						() -> mock(AuthenticationTokenProvider.class))
				.run(context -> {

					assertThat(context)
							.getBean(BearerTokenPropagator.class)
							.isInstanceOf(DefaultBearerTokenPropagator.class);
				});
	}

	@Test
	void shouldCreateDefaultHeaderContextPropagatorWhenEnabled() {

		contextRunner.run(context -> {

			assertThat(context)
					.getBean(HeaderContextPropagator.class)
					.isInstanceOf(DefaultHeaderContextPropagator.class);
		});
	}

	@Test
	void shouldBackOffWhenInterceptorAlreadyExists() {

		contextRunner
				.withBean(
						RestClientInterceptor.class,
						() -> mock(RestClientInterceptor.class))
				.run(context ->

				assertThat(context)
						.hasSingleBean(RestClientInterceptor.class));
	}

	@Test
	void shouldBackOffWhenHeaderContextPropagatorAlreadyExists() {

		contextRunner
				.withBean("customHeaderContextPropagator",
						HeaderContextPropagator.class,
						() -> mock(HeaderContextPropagator.class))
				.run(context -> {

					assertThat(context)
							.hasSingleBean(HeaderContextPropagator.class);
				});
	}

	@Test
	void shouldBackOffWhenBearerTokenPropagatorAlreadyExists() {

		contextRunner
				.withBean("customBearerTokenPropagator",
						BearerTokenPropagator.class,
						() -> mock(BearerTokenPropagator.class))
				.run(context -> {

					assertThat(context)
							.hasSingleBean(BearerTokenPropagator.class);
				});
	}
}