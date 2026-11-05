
package com.financetracker.finance.controllers;

import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.finance.dto.request.CreateBudgetRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.BudgetResponse;
import com.financetracker.finance.services.BudgetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<BudgetResponse>>> getBudgets(
            @RequestParam YearMonth month) {

        List<BudgetResponse> budgets = budgetService.getBudgets(month.atDay(1));

        return ResponseEntity.ok(ApiResponseDto.<List<BudgetResponse>>builder().data(budgets).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<BudgetResponse>> createBudget(
            @Valid @RequestBody CreateBudgetRequest request) {

        BudgetResponse response = budgetService.createBudget(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.<BudgetResponse>builder().data(response).build());
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponseDto<BudgetResponse>> updateBudget(
            @PathVariable String budgetId,
            @Valid @RequestBody CreateBudgetRequest request) {

        BudgetResponse response = budgetService.updateBudget(budgetId, request);

        return ResponseEntity.ok(ApiResponseDto.<BudgetResponse>builder().data(response).build());
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBudget(
            @PathVariable String budgetId) {

        budgetService.deleteBudget(budgetId);

        return ResponseEntity.noContent().build();
    }
}
