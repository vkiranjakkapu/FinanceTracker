package com.financetracker.finance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        LocalDate month,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        List<CategoryExpenseResponse> categoryExpenses,
        List<MonthlyTrendResponse> monthlyTrends,
        List<TransactionResponse> recentTransactions,
        List<BudgetUtilizationResponse> budgetUtilization
) {}