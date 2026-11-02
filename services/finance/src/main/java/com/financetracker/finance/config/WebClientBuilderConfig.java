package com.financetracker.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientBuilderConfig {

    @Bean
    WebClient.Builder eurekaWebClientBuilder() {
        return WebClient.builder();
    }

}
