package com.financetracker.finance.enums;

import com.platform.web.exception.ErrorDefinition;

public enum BusinessErrors implements ErrorDefinition {
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "BUS-0001", "This resource already exists in records."),
    INVALID_REQUEST("INVALID_REQUEST", "BUS-0002", "Request can't be fulfilled."),
    INVALID_BUDGET_MONTH("INVALID_BUDGET_MONTH", "BUS-0003", "Budget month must be the first day of the month."),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "BUS-0004", "Resource not found"),
    BUDGET_CATEGORY_MUST_BE_EXPENSE("BUDGET_CATEGORY_MUST_BE_EXPENSE", "BUS-0005",
            "Budgets can only be created for expense categories."),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", "BUS-0006", "From date cannot be after to date.");

    private String errorName;
    private String errorCode;
    private String errorMessage;

    BusinessErrors(String errorName,
            String errorCode,
            String errorMessage) {
        this.errorName = errorName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorName() {
        return this.errorName;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

}
