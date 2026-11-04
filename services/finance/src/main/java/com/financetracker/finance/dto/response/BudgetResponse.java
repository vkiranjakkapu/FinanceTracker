package com.financetracker.finance.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BudgetResponse(
        String id,
        String categoryId,
        String categoryName,
        LocalDate month,
        BigDecimal amount,
        Instant createdAt,
        Instant updatedAt
) {}