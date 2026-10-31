# Logging Starter

Production-ready Spring Boot starter providing request context management, correlation IDs, request logging, response logging, and exception logging.

## Features

- Request context lifecycle management
- Correlation ID generation and propagation
- Request logging
- Response logging
- Exception logging
- Transparent exception logging through Spring AOP
- Rolling file logging
- Modular Logback configuration
- Request header mapping and correlation header support
- Spring Boot auto-configuration
- Zero-configuration defaults

## Installation

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>logging</artifactId>
</dependency>
```

## Design Principles

- Convention over Configuration
- Single Responsibility
- Constructor Injection
- Interface-first design
- Modular Logback resources
- Zero-configuration defaults

## Configuration

| Property                                          | Default        |
| ------------------------------------------------- | -------------- |
| `platform.logging.request-context`                | `true`         |
| `platform.logging.level`                          | `INFO`         |
| `platform.logging.pattern.console`                | `default`      |
| `platform.logging.pattern.file`                   | `default`      |
| `platform.logging.exception-logging.enabled`      | `true`         |
| `platform.logging.request.enabled`                | `true`         |
| `platform.logging.request.include-query-string`   | `true`         |
| `platform.logging.request.include-client-ip`      | `false`        |
| `platform.logging.request.excluded-paths`         | `none`         |
| `platform.logging.file.directory`                 | `logs`         |
| `platform.logging.file.name`                      | `application`  |
| `platform.logging.file.max-size`                  | `100MB`        |
| `platform.logging.file.max-history`               | `30`           |
| `platform.logging.file.total-size-cap`            | `5GB`          |

## Auto-configured Beans

- `RequestContextHolder`
- `CorrelationIdGenerator`
- `CorrelationIdFilter`
- `RequestLoggingFilter`
- `ExceptionLogger`
- `ExceptionLoggingAspect`

## Request Context

The logging starter now includes centralized request-context support:

- Request-scoped context management via `RequestContextHolder`
- MDC-backed storage for thread-safe request state
- Standardized request header propagation
- Correlation ID lifecycle management for distributed tracing
- Request context clearing after the request completes

### Standard Keys

- `correlationId` — unique identifier for the current request

### Standard Header

- `X-Correlation-Id` — propagated correlation identifier

## What it provides

- Default Logback configuration
- Console appender
- Rolling file appender
- Request context and correlation ID management
- Request/response logging and exception logging

## Logging Architecture

```text
Application
      │
      ▼
LoggingProperties
      │
      ▼
defaults.xml
      │
 ┌────┴────┐
 ▼         ▼
Console  Rolling File
Appender   Appender
```

## Resource Structure

```text
platform/
  └── logging/
        ├── defaults.xml
        ├── appenders/
        │     ├── console.xml
        │     └── file.xml
        └── patterns/
              └── default.xml
```

## Customization Points

```text
| Customization                        | Supported |
| ------------------------------------ | --------- |
| Override log level                   | ✅         |
| Override log directory               | ✅         |
| Override log file name               | ✅         |
| Override rolling policy properties   | ✅         |
| Override log pattern                 | ✅         |
| Replace entire Logback configuration | ✅         |
```

## Providing Custom Log Pattern

```xml
<configuration>

    <property
        name="DEFAULT_LOG_PATTERN"
        value="%d %-5level [%thread] %msg%n"/>

    <include resource="platform/logging/defaults.xml"/>

</configuration>
```

## Examples

Incoming request

```
GET /api/v1/users
```

↓

Logs

```text
REQ GET /api/v1/users | corr=4a576e83-b5e6-4978-927c-c68f7f639058
```

↓

Outgoing response

```text
RES GET /api/v1/users | status=200 | 10 ms | corr=4a576e83-b5e6-4978-927c-c68f7f639058
```

## Thread Safety

All provided beans are stateless and singleton-safe. Request-scoped data is managed through MDC-backed request context storage.

## Testing

Covered by:

- Unit Tests
- AutoConfiguration Tests

## Philosophy

Platform does not replace Spring.

It provides business-oriented abstractions built on top of proven Spring infrastructure, allowing services to remain focused on domain logic while benefiting from consistent, production-ready platform capabilities.

