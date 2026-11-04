package com.financetracker.finance.services;

import java.time.LocalDate;
import java.util.List;

import com.financetracker.finance.dto.request.CreateBudgetRequest;
import com.financetracker.finance.dto.response.BudgetResponse;

public interface BudgetService {

    BudgetResponse createBudget(CreateBudgetRequest request);

    List<BudgetResponse> getBudgets(LocalDate month);

    BudgetResponse updateBudget(
            String budgetId,
            CreateBudgetRequest request);

    void deleteBudget(String budgetId);
}