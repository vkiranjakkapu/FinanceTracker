# Quiz & Online Assessments Platform - Identity Service

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)

The **Identity Service** is the authentication and authorization component of the Quiz & Online Assessments Platform. It is responsible for managing users, authentication, authorization, role management, and JWT-based security.

This project follows modern backend development practices using **Spring Boot**, **Spring Security**, **JWT**, **PostgreSQL**, and **RESTful APIs**.

# Features

- JWT Authentication
- Refresh Token Support
- Secure Logout
- Role-Based Access Control (RBAC)
- User Management
- Bean Validation
- Soft Delete
- Global Exception Handling
- Swagger/OpenAPI Documentation
- Unit Testing
- Controller Integration Testing

# Technology Stack

| Technology | Version |
|------------|----------|
| Java | 25 |
| Spring Boot | 4.1.0 |
| Spring Security | Latest |
| Spring Data JPA | Latest |
| PostgreSQL | 18 |
| Maven | Latest |
| JWT | jjwt |
| Swagger/OpenAPI | springdoc-openapi |
| JUnit 5 | Latest |
| Mockito | Latest |
| MockMvc | Latest |
| Docker | Supported |

# Architecture

```text
                +----------------------+
                |      REST Client     |
                +----------+-----------+
                           |
                           |
                    Spring Boot API
                           |
         +-----------------+------------------+
         |                                    |
 Authentication                    User Management
         |                                    |
 Spring Security                  Spring Data JPA
         |                                    |
         +-----------------+------------------+
                           |
                     PostgreSQL Database
```

# Authentication Flow

```
Client
   |
   | Login
   v
Identity Service
   |
Validate Credentials
   |
Generate Access Token
Generate Refresh Token
   |
Return Tokens
```

# Project Structure

```
src
└── main
    ├── config
    ├── controller
    ├── dto
    ├── entities
    ├── exception
    ├── repository
    └── service
```

# API Endpoints

## Authentication

| Method | Endpoint | Description |
|----------|----------|-------------|
| POST | /api/auth/login | Authenticate user |
| POST | /api/auth/refresh | Refresh JWT |
| POST | /api/auth/logout | Logout user |

## User Management

| Method | Endpoint |
|----------|----------|
| POST | /api/users |
| POST | /api/users/search |
| GET | /api/users/me |
| GET | /api/users |
| GET | /api/users/role/{role} |
| GET | /api/users/{id} |
| PUT | /api/users/{id} |
| PATCH | /api/users/ |
| DELETE | /api/users/{id} |

# Security

- Stateless Authentication
- JWT Access Tokens
- Refresh Token Rotation
- Role-Based Authorization
- Password Encryption using BCrypt

# Validation

Request validation is implemented using Jakarta Bean Validation.

Examples include:

- @NotBlank
- @NotNull
- @Email

# Testing

The project includes comprehensive testing for both business logic and REST APIs.

### Unit Tests

- AuthenticationService
- JwtService
- UserService

### Controller Tests

- AuthController
- UserController

Testing Frameworks

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test

# Architectural Decision Records

See the ADR documentation under:

```
docs/adr
```
