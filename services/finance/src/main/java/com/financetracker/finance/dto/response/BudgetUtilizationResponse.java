package com.financetracker.finance.dto.response;

import java.math.BigDecimal;

public record BudgetUtilizationResponse(
        String categoryId,
        String categoryName,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercentage
) {}