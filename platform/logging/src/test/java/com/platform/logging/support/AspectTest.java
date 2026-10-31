package com.platform.logging.support;

import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;

public final class AspectTest {

    private AspectTest() {
    }

    public static void mockInvocation(
            ProceedingJoinPoint joinPoint,
            Object response,
            Object... args) throws Throwable {

        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(response);
    }
}