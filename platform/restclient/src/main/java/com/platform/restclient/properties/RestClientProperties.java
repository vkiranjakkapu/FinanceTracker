package com.platform.restclient.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform.rest-client")
public class RestClientProperties {

    private final PropagationProperties propagation = new PropagationProperties();

    public PropagationProperties getPropagation() {
        return propagation;
    }

    public static class PropagationProperties {

        private final ContextProperties context = new ContextProperties();

        private final BearerTokenProperties bearerToken = new BearerTokenProperties();

        public ContextProperties getContext() {
            return context;
        }

        public BearerTokenProperties getBearerToken() {
            return bearerToken;
        }
    }

    public static class ContextProperties {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class BearerTokenProperties {

        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}