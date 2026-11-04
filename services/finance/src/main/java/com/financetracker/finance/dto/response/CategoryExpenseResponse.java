package com.financetracker.finance.dto.response;

import java.math.BigDecimal;

public record CategoryExpenseResponse(
        String categoryId,
        String categoryName,
        BigDecimal amount
) {}