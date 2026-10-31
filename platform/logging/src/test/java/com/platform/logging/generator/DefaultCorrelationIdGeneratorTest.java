package com.platform.logging.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

class DefaultCorrelationIdGeneratorTest {

    private DefaultCorrelationIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultCorrelationIdGenerator();
    }

    @Test
    void shouldGenerateCorrelationId() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        String correlationId = generator.generate(request);

        assertThat(correlationId)
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldGenerateUniqueCorrelationIds() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        String first = generator.generate(request);
        String second = generator.generate(request);

        assertThat(first)
                .isNotEqualTo(second);
    }

    @Test
    void shouldGenerateUuidFormattedCorrelationId() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        String correlationId = generator.generate(request);

        assertThatCode(() -> java.util.UUID.fromString(correlationId))
                .doesNotThrowAnyException();
    }
}