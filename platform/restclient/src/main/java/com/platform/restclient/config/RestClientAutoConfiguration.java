package com.platform.restclient.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;

import com.platform.logging.holder.RequestContextHolder;
import com.platform.logging.properties.LoggingProperties;
import com.platform.restclient.customizer.DefaultRestClientCustomizer;
import com.platform.restclient.interceptor.RestClientInterceptor;
import com.platform.restclient.propagation.BearerTokenPropagator;
import com.platform.restclient.propagation.DefaultBearerTokenPropagator;
import com.platform.restclient.propagation.DefaultHeaderContextPropagator;
import com.platform.restclient.propagation.HeaderContextPropagator;
import com.platform.restclient.propagation.NoOpBearerTokenPropagator;
import com.platform.restclient.propagation.NoOpHeaderContextPropagator;
import com.platform.restclient.properties.RestClientProperties;
import com.platform.security.authentication.AuthenticationTokenProvider;
import com.platform.security.properties.SecurityProperties;

@AutoConfiguration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "platform.rest-client.propagation.context", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(HeaderContextPropagator.class)
    public HeaderContextPropagator headerContextPropagator(
            LoggingProperties properties,
            RequestContextHolder requestContextHolder) {

        return new DefaultHeaderContextPropagator(properties, requestContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean(HeaderContextPropagator.class)
    public HeaderContextPropagator noOpHeaderContextPropagator() {
        return new NoOpHeaderContextPropagator();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientInterceptor headerContextPropagationInterceptor(
            HeaderContextPropagator headerContextPropagator,
            BearerTokenPropagator bearerTokenPropagator) {

        return new RestClientInterceptor(headerContextPropagator, bearerTokenPropagator);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientCustomizer restClientCustomizer(
            RestClientInterceptor interceptor) {

        return new DefaultRestClientCustomizer(interceptor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "platform.rest-client.propagation.bearer-token", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(BearerTokenPropagator.class)
    public BearerTokenPropagator bearerTokenPropagator(
            AuthenticationTokenProvider authenticationTokenProvider,
            SecurityProperties securityProperties) {

        return new DefaultBearerTokenPropagator(authenticationTokenProvider, securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean(BearerTokenPropagator.class)
    public BearerTokenPropagator noOpBearerTokenPropagator() {
        return new NoOpBearerTokenPropagator();
    }

}