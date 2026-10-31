package com.platform.web.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.platform.web.handler.DefaultWebExceptionHandler;

class WebAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(
							WebAutoConfiguration.class));

	@Test
	void shouldCreateDefaultWebExceptionHandlerWhenEnabled() {

		contextRunner.run(context ->

		assertThat(context)
				.hasSingleBean(DefaultWebExceptionHandler.class));
	}

	@Test
	void shouldNotCreateDefaultWebExceptionHandlerWhenDisabled() {

		contextRunner
				.withPropertyValues(
						"platform.web.enabled=false")
				.run(context ->

				assertThat(context)
						.doesNotHaveBean(
								DefaultWebExceptionHandler.class));
	}
}