package com.platform.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.platform.web.handler.DefaultWebExceptionHandler;

@AutoConfiguration
public class WebAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "platform.web", name = "enabled", havingValue = "true", matchIfMissing = true)
    @Import(DefaultWebExceptionHandler.class)
    static class ExceptionHandlingConfiguration {
    }

}
