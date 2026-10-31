# RestClient Starter module

Spring Boot starter module providing standardized request context propagation for `RestClient`. The RestClient module does not define request headers or request context.
Instead, it consumes the standardized request context provided by the Logging module and propagates it to outgoing HTTP requests.

## Features

- Standardized request context propagation
- Automatic RestClient customization
- Spring Boot auto-configuration
- Zero application code required
- Optional bearer token propagation

## Installation

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>restclient</artifactId>
</dependency>
```

## Depends On

- request-context

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `platform.rest-client.propagation.context.enabled` | `true` | Enable request context propagation |
| `platform.rest-client.propagation.bearer-token.enabled` | `false` | Enable bearer token propagation |

---

## Auto-configured Beans

- HeaderContextPropagator
- BearerTokenPropagator
- RestClientInterceptor
- RestClientCustomizer

## How it works

```
Application starts
      ↓
Spring creates `RestClient.Builder`
      ↓
Spring discovers `RestClientCustomizer`
      ↓
Customizer executes (attaches interceptor to client)
      ↓
Builder now contains `HeaderContextPropagationInterceptor`
      ↓
Application later calls `restClient.get()`
      ↓
Spring sees `Interceptor exists`
      ↓
Interceptor executes
      ↓
Calls `HeaderContextPropagator`
      ↓
HeaderContextPropagator copies request-context headers
      ↓
BearerTokenPropagator optionally propagates the authenticated bearer token
      ↓
HTTP request leaves
```

## Example

Incoming request

```
X-Correlation-Id: abc123
```

↓

Outgoing RestClient request

```
X-Correlation-Id: abc123
Authorization: Bearer eyJ...
```

## Design Principles

- Delegates request context propagation to `HeaderContextPropagator`
- Delegates bearer token propagation to `BearerTokenPropagator`
- Obtains authentication tokens through `AuthenticationTokenProvider` without directly depending on Spring Security
- Uses `RequestContextHolder` as the single source of request context
- Stateless
- Thread-safe
- Constructor Injection
- Spring Boot native auto-configuration

## Testing

Covered by:

- Unit Tests
- AutoConfiguration Tests

## Philosophy

Platform does not replace Spring.

It provides business-oriented abstractions built on top of proven Spring infrastructure, allowing services to remain focused on domain logic while benefiting from consistent, production-ready platform capabilities.