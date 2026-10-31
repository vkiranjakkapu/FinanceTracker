package com.platform.security.properties;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.HttpHeaders;

import com.platform.security.constants.SecurityConstants;

import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "platform.security")
public class SecurityProperties {

    private boolean enabled = true;

    private final AuthenticationProperties authentication = new AuthenticationProperties();

    private final AuthorizationProperties authorization = new AuthorizationProperties();

    private final JwtProperties jwt = new JwtProperties();

    private List<String> publicPaths = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**");

    private List<String> adminPaths = List.of("/actuator/**");

    private final CorsProperties cors = new CorsProperties();

    public static class AuthenticationProperties {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

    public static class AuthorizationProperties {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class JwtProperties {
        private boolean enabled = true;

        private static final String headerName = HttpHeaders.AUTHORIZATION;

        private static final String tokenPrefix = SecurityConstants.BEARER_PREFIX.trim();

        /**
         * Secret used for HMAC verification.
         * Temporary for development.
         */
        private String secret;

        /**
         * Expected JWT issuer.
         */
        private String issuer = "financetracker";

        /**
         * Allowed clock skew.
         */
        private Duration clockSkew = Duration.ofSeconds(30);

        private String usernameClaim = "username";

        private String emailClaim = "email";

        private String authoritiesClaim = "roles";

        private String authorityPrefix = "ROLE_";

        @DurationUnit(ChronoUnit.MILLIS)
        private Duration accessTokenExpiration = Duration.ofMinutes(15);

        @DurationUnit(ChronoUnit.MILLIS)
        private Duration refreshTokenExpiration = Duration.ofDays(7);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return headerName;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Duration getClockSkew() {
            return clockSkew;
        }

        public void setClockSkew(Duration clockSkew) {
            this.clockSkew = clockSkew;
        }

        public String getUsernameClaim() {
            return usernameClaim;
        }

        public void setUsernameClaim(String usernameClaim) {
            this.usernameClaim = usernameClaim;
        }

        public String getEmailClaim() {
            return emailClaim;
        }

        public void setEmailClaim(String emailClaim) {
            this.emailClaim = emailClaim;
        }

        public String getAuthoritiesClaim() {
            return authoritiesClaim;
        }

        public void setAuthoritiesClaim(String authoritiesClaim) {
            this.authoritiesClaim = authoritiesClaim;
        }

        public String getAuthorityPrefix() {
            return authorityPrefix;
        }

        public void setAuthorityPrefix(String authorityPrefix) {
            this.authorityPrefix = authorityPrefix;
        }

        public Duration getAccessTokenExpiration() {
            return accessTokenExpiration;
        }

        public void setAccessTokenExpiration(Duration accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
        }

        public Duration getRefreshTokenExpiration() {
            return refreshTokenExpiration;
        }

        public void setRefreshTokenExpiration(Duration refreshTokenExpiration) {
            this.refreshTokenExpiration = refreshTokenExpiration;
        }
    }

    public static class CorsProperties {

        private boolean enabled = true;

        private List<String> allowedOriginPatterns = List.of("http://localhost:4200");

        private List<String> allowedMethods = List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

        private List<String> allowedHeaders = List.of("*");

        private List<String> exposedHeaders = List.of();

        private boolean allowCredentials = true;

        private long maxAge = 3600;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getAllowedOriginPatterns() {
            return allowedOriginPatterns;
        }

        public void setAllowedOriginPatterns(List<String> allowedOrigins) {
            this.allowedOriginPatterns = allowedOrigins;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(long maxAge) {
            this.maxAge = maxAge;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AuthenticationProperties getAuthentication() {
        return authentication;
    }

    public AuthorizationProperties getAuthorization() {
        return authorization;
    }

    public JwtProperties getJwt() {
        return jwt;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public List<String> getAdminPaths() {
        return adminPaths;
    }

    public void setAdminPaths(List<String> adminPaths) {
        this.adminPaths = adminPaths;
    }

}
