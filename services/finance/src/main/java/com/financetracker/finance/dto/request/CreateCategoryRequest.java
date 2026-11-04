package com.financetracker.finance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.financetracker.finance.domain.model.TransactionType;

public record CreateCategoryRequest(

        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        TransactionType type,

        @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$")
        String color,

        @Size(max = 100)
        String description,

        @Size(max = 30)
        String icon

) {}