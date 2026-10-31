# Quiz & Online Assessments Platform Web Starter

Production-ready Spring Boot starter providing standardized HTTP error responses and framework exception handling for Quiz & Online Assessments Platform services.

## Features

- Standardized HTTP error responses
- Centralized framework exception handling
- Bean Validation error mapping
- Standard error definitions
- Extensible error definition contract
- Spring Boot auto-configuration
- Configurable enable/disable support

## Architecture

```
                    HTTP Request
                         │
                         ▼
                  Spring MVC
                         │
                         ▼
         DefaultWebExceptionHandler
                         │
         ┌───────────────┴────────────────┐
         │                                │
 Framework Exceptions             Service Exceptions
         │                                │
         ▼                                ▼
 Standard ErrorResponse        Service Exception Handler
```

```text
Controller
      │
      ▼
Business Exception
      │
      ▼
@RestControllerAdvice
      │
      ├──────────────► Logging Aspect
      │                     │
      ▼                     ▼
ErrorResponse         ExceptionLogger
```

The Web starter standardizes framework exception handling while allowing services to implement their own business exception handlers.

Exception logging is intentionally delegated to the platform logging module.

## Installation

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>web</artifactId>
</dependency>
```

## Configuration

```yaml
platform:
  web:
    enabled: true
```

| Property | Default | Description |
|----------|---------|-------------|
| `platform.web.enabled` | `true` | Enables the Web starter |

## Error Model

The starter provides a standard error response model.

```json
{
  "errorName": "VALIDATION_ERROR",
  "errorCode": "4001",
  "errorMessage": "Validation failed.",
  "validationErrors": [
    {
      "field": "email",
      "rejectedValue": "abc",
      "message": "must be a valid email"
    }
  ],
  "timestamp": "2026-07-10T10:15:30"
}
```

## Built-in Error Definitions

### WebExceptions

- INTERNAL_SERVER_ERROR
- BAD_REQUEST
- RESOURCE_NOT_FOUND

### ValidationExceptions

- VALIDATION_ERROR
- APPLICATION_ERROR

## Extending Error Definitions

Applications can introduce additional domain-specific error definitions without modifying the platform.

```java
public enum UserExceptions implements ErrorDefinition {

    USER_NOT_FOUND(
            "USER_NOT_FOUND",
            "USR-1001",
            "User not found."),

    USER_ALREADY_EXISTS(
            "USER_ALREADY_EXISTS",
            "USR-1002",
            "User already exists.");

}
```

These definitions can be used directly with `ErrorResponse`.

```java
throw new UserNotFoundException(
        new ErrorResponse(UserExceptions.USER_NOT_FOUND));

throw new UserAlreadyExistsException(
        new ErrorResponse(
                UserExceptions.USER_ALREADY_EXISTS,
                "User already exists with email xyz@example.com"));
```

## Exception Handling

The Web starter automatically translates common Spring MVC framework exceptions into standardized HTTP responses.

Supported framework exceptions include:

- MethodArgumentNotValidException
- BindException
- MissingServletRequestParameterException
- MethodArgumentTypeMismatchException
- HttpMessageNotReadableException
- IllegalArgumentException
- Exception (fallback)

Business exceptions remain the responsibility of consuming services.

## Logging Integration

The Web starter intentionally **does not perform exception logging**.

Exception logging is handled transparently by the platform logging module through an Aspect that intercepts centralized exception handlers.

This separation keeps responsibilities clear:

- **Web** translates exceptions into HTTP responses.
- **Logging** records operational information.
- **Services** define business exceptions.

This guarantees that exception translation and exception logging evolve independently.

## Usage

Simply include the starter dependency.

Framework exceptions are automatically translated into standardized error responses.

Applications may define their own `@RestControllerAdvice` for business exceptions without affecting platform exception handling.

## Extension Points

Applications may extend the platform by:

- Implementing additional `ErrorDefinition` enums
- Creating custom business exceptions
- Creating service-specific `@RestControllerAdvice`

No platform modification is required.

## Engineering Principles

The Web starter follows these principles:

- Translate framework exceptions only.
- Leave business exception handling to services.
- Never perform exception logging.
- Keep HTTP response generation separate from operational logging.
- Keep the module independent of logging concerns.
- Prefer immutable models.
- Keep the platform extensible through interfaces.

## Testing

The starter includes tests covering:

- Error response construction
- Error definition implementations
- Framework exception handling
- Auto-configuration
- Demo service verification

## Philosophy

This platform does not replace Spring.

The Web starter provides a standardized HTTP exception translation layer built on top of Spring MVC while leaving business exception handling to applications and operational logging to the Logging starter.

Each concern remains independent, resulting in a modular, maintainable, and production-ready platform.