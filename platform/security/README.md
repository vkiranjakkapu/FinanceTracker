# Security Module

Production-ready Spring Boot starter providing authentication, authorization and security abstractions built on top of Spring Security.


## Overview

This module builds upon Spring Security instead of replacing it.

The starter provides opinionated, production-ready defaults for JWT authentication, method security, exception handling, request context enrichment and security abstractions while allowing services to customize behaviour through Spring Boot configuration and bean overrides.

Business services interact with simple module abstractions instead of directly depending on Spring Security APIs.


## Features

- JWT Resource Server
- Stateless authentication
- Method-level authorization (`@PreAuthorize`)
- Configurable JWT claim mapping
- Authentication abstraction
- Authenticated user abstraction
- Request Context enrichment
- AuthenticationTokenProvider for infrastructure integrations
- Custom authentication entry point
- Custom access denied handler
- Configurable CORS
- Spring Boot auto-configuration
- Feature toggles
- Extension points through conditional beans


## Architecture

```text
                    HTTP Request
                          │
                          ▼
                 Spring Security Filter Chain
                          │
                 Bearer Token Authentication
                          │
                          ▼
                 SecurityContextHolder
                          │
                          ▼
              AuthenticationContext
                          │
                          ▼
                AuthenticatedUser
                          │
                          ▼
      AuthenticationContextInitializerFilter
                          │
                          ▼
                 RequestContextHolder
                          │
          ┌───────────────┼────────────────┐
          ▼               ▼                ▼
      Logging         RestClient       Business Services
                          │
                          ▼
            AuthenticationTokenProvider
```


## Installation

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>security</artifactId>
</dependency>
```


## Configuration

### Minimal Configuration

```yaml
platform:
  security:
    jwt:
      secret: your-secret-key
```


### Complete Example

```yaml
platform:
  security:
    enabled: true

    public-paths:
      - /api/auth/**
      - /swagger-ui/**
      - /v3/api-docs/**

    admin-paths:
      - /actuator/**

    jwt:
      enabled: true
      secret: your-secret-key
      issuer: financetracker
      authorities-claim: roles
      authority-prefix: ROLE_
      username-claim: username
      user-id-claim: sub

    cors:
      enabled: true
      allowed-origin-patterns:
        - http://localhost:3000
```


## Usage

### AuthenticationContext

```java
@RestController
class UserController {

    private final AuthenticationContext authenticationContext;

    UserController(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @GetMapping("/me")
    public AuthenticatedUser me() {
        return authenticationContext.getCurrentUser()
                .orElseThrow();
    }
}
```


### AuthenticationTokenProvider

Infrastructure components such as the RestClient use `AuthenticationTokenProvider` to obtain the current bearer token for downstream propagation. This abstraction is intended for platform infrastructure and is not required by business services.

### Method Security

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public String admin() {
    return "Only administrators can access this endpoint.";
}
```


## Extension Points

The following beans may be overridden by services when customization is required.

- AuthenticationAdapter
- AuthenticationContext
- JwtDecoder
- JWT Authentication Converter
- AuthenticationEntryPoint
- AccessDeniedHandler

This module automatically backs off when a custom implementation is provided.


## Examples

JWT Payload

```json
{
  "sub": "1234567890",
  "username": "venkat",
  "roles": [
    "ADMIN",
    "USER"
  ],
  "iss": "financetracker"
}
```


## Testing

The starter includes:

- Unit tests
- Auto-configuration tests
- Consumer integration verification

Security integration is validated through a consuming Spring Boot service to ensure the starter behaves exactly as it would in production.


## Design Principles

- Built on Spring Security
- Interface-first design
- Separation of business and infrastructure abstractions
- AuthenticationContext for business access
- AuthenticationTokenProvider for infrastructure integrations

- Stateless authentication
- Constructor injection
- Spring Boot native auto-configuration
- Fail-fast configuration validation
- Request Context integration
- Production-ready defaults
- Easily extensible


## Philosophy

Platform does not replace Spring.

It provides business-oriented abstractions built on top of proven Spring infrastructure, allowing services to remain focused on domain logic while benefiting from consistent, production-ready platform capabilities.