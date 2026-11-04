package com.financetracker.finance.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBudgetRequest(

        @NotBlank
        String categoryId,

        @NotNull
        LocalDate month,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount

) {}