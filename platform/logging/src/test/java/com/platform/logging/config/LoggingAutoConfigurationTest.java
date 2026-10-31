package com.platform.logging.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.platform.logging.aspect.ExceptionLoggingAspect;
import com.platform.logging.exception.ExceptionLogger;
import com.platform.logging.filter.CorrelationIdFilter;
import com.platform.logging.filter.RequestLoggingFilter;

public class LoggingAutoConfigurationTest {
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					LoggingAutoConfiguration.class
			));

	@Test
	void shouldCreateDefaultBeans() {
		contextRunner.run(context -> {
			assertThat(context).hasSingleBean(RequestLoggingFilter.class);
			assertThat(context).hasSingleBean(CorrelationIdFilter.class);
			assertThat(context).hasBean("requestLoggingFilterRegistration");
			assertThat(context).hasBean("correlationIdFilterRegistration");
			assertThat(context).hasSingleBean(ExceptionLogger.class);
			assertThat(context).hasSingleBean(ExceptionLoggingAspect.class);
		});
	}

	@Test
	void shouldDisableRequestLoggingFilter() {
		contextRunner.withPropertyValues("platform.logging.request.enabled=false")
				.run(context -> {
					assertThat(context).doesNotHaveBean(RequestLoggingFilter.class);
				});
	}

	@Test
	void shouldDisableExceptionLogging() {
		contextRunner.withPropertyValues("platform.logging.exception-logging.enabled=false")
				.run(context -> {
					assertThat(context).doesNotHaveBean(ExceptionLogger.class);
					assertThat(context).doesNotHaveBean(ExceptionLoggingAspect.class);
				});
	}

	@Test
	void shouldDisableCorrelationIdFilter() {
		contextRunner.withPropertyValues("platform.request-context.enabled=false")
				.run(context -> {
					assertThat(context).doesNotHaveBean(CorrelationIdFilter.class);
				});
	}

	@Test
	void shouldBackOffWhenCustomExceptionLoggerExists() {
		ExceptionLogger customLogger = mock(ExceptionLogger.class);
		contextRunner
				.withBean(ExceptionLogger.class, () -> customLogger)
				.run(context -> {
					assertThat(context).hasSingleBean(ExceptionLogger.class);
					assertThat(context).getBean(ExceptionLogger.class).isSameAs(customLogger);
					assertThat(context).hasSingleBean(ExceptionLoggingAspect.class);
				});
	}
}