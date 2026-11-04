package com.financetracker.finance.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.financetracker.finance.domain.model.TransactionType;

public record TransactionResponse(
        String id,
        TransactionType type,
        BigDecimal amount,
        String description,
        String categoryId,
        String categoryName,
        LocalDate transactionDate,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}