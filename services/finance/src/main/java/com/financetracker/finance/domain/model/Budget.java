package com.financetracker.finance.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "budgets")
@CompoundIndex(name = "uk_budget_owner_category_month", def = "{'ownerId': 1, 'categoryId': 1, 'month': 1}", unique = true)
public class Budget {

    @Id
    private String id;

    private String ownerId;

    private String categoryId;

    private String categoryName;

    // Always store the first day of the budget month.
    // Example: 2026-11-01 represents November 2026.
    private LocalDate month;

    private BigDecimal amount;

    private Instant createdAt;

    private Instant updatedAt;
}