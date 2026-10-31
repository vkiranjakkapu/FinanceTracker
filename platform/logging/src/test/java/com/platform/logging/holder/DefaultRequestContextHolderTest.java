package com.platform.logging.holder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultRequestContextHolderTest {

    private static final String KEY = "correlationId";
    private static final String VALUE = "abc123";

    private DefaultRequestContextHolder holder;

    @BeforeEach
    void setUp() {
        holder = new DefaultRequestContextHolder();
    }

    @AfterEach
    void tearDown() {
        holder.clear();
    }

    @Test
    void shouldStoreValue() {

        holder.put(KEY, VALUE);

        assertThat(holder.get(KEY))
                .contains(VALUE);
    }

    @Test
    void shouldRemoveValue() {

        holder.put(KEY, VALUE);

        holder.remove(KEY);

        assertThat(holder.get(KEY))
                .isEmpty();
    }

    @Test
    void shouldClearContext() {

        holder.put(KEY, VALUE);

        holder.clear();

        assertThat(holder.get(KEY))
                .isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenKeyDoesNotExist() {

        assertThat(holder.get(KEY))
                .isEmpty();
    }
}