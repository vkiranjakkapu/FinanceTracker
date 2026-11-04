package com.financetracker.finance.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.financetracker.finance.domain.model.TransactionType;

public record CreateTransactionRequest(

        @NotNull
        TransactionType type,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank
        @Size(max = 120)
        String description,

        @NotBlank
        String categoryId,

        @NotNull
        LocalDate transactionDate,

        @Size(max = 500)
        String notes

) {}