package com.platform.logging.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.platform.logging.exception.ExceptionLogger;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
public class ExceptionLoggingAspect {

    private final ExceptionLogger exceptionLogger;

    public ExceptionLoggingAspect(ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.ExceptionHandler) && " +
            "(@within(org.springframework.web.bind.annotation.ControllerAdvice) || " +
            "@within(org.springframework.web.bind.annotation.RestControllerAdvice))")
    public Object logException(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response = joinPoint.proceed();

        Exception exception = findArgument(joinPoint, Exception.class);
        HttpServletRequest request = currentRequest();

        if (exception != null
                && request != null
                && response instanceof ResponseEntity<?> responseEntity) {

            exceptionLogger.log(
                    exception,
                    request,
                    responseEntity.getStatusCode());
        }

        return response;
    }

    private <T> T findArgument(ProceedingJoinPoint joinPoint, Class<T> type) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return attributes != null ? attributes.getRequest() : null;
    }
}