package com.financetracker.finance.dto.response;

import com.financetracker.finance.domain.model.TransactionType;

public record CategoryResponse(
        String id,
        String name,
        TransactionType type,
        String color,
        String icon,
        String description,
        boolean isDefault
) {}