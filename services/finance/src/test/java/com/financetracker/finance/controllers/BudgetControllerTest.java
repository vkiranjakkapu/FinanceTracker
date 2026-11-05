package com.financetracker.finance.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.financetracker.finance.dto.request.CreateBudgetRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.BudgetResponse;
import com.financetracker.finance.services.BudgetService;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private BudgetResponse budgetResponse() {
        return new BudgetResponse(
                "budget-1",
                "category-1",
                "Food",
                LocalDate.of(2026, 11, 1),
                new BigDecimal("5000.00"),
                Instant.parse("2026-11-01T10:00:00Z"),
                Instant.parse("2026-11-01T10:00:00Z"));
    }

    private CreateBudgetRequest createRequest() {
        return new CreateBudgetRequest(
                "category-1",
                LocalDate.of(2026, 11, 1),
                new BigDecimal("5000.00"));
    }

    @Test
    void getBudgets_ShouldReturnBudgetsForMonth() {

        YearMonth month = YearMonth.of(2026, 11);
        List<BudgetResponse> budgets = List.of(budgetResponse());

        when(budgetService.getBudgets(LocalDate.of(2026, 11, 1)))
                .thenReturn(budgets);

        ResponseEntity<ApiResponseDto<List<BudgetResponse>>> response =
                budgetController.getBudgets(month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgets, response.getBody().data());

        verify(budgetService).getBudgets(LocalDate.of(2026, 11, 1));
    }

    @Test
    void createBudget_ShouldReturnCreatedResponse() {

        CreateBudgetRequest request = createRequest();
        BudgetResponse expected = budgetResponse();

        when(budgetService.createBudget(request)).thenReturn(expected);

        ResponseEntity<ApiResponseDto<BudgetResponse>> response =
                budgetController.createBudget(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(budgetService).createBudget(request);
    }

    @Test
    void updateBudget_ShouldReturnUpdatedResponse() {

        CreateBudgetRequest request = createRequest();
        BudgetResponse expected = budgetResponse();

        when(budgetService.updateBudget("budget-1", request))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<BudgetResponse>> response =
                budgetController.updateBudget("budget-1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(budgetService).updateBudget("budget-1", request);
    }

    @Test
    void deleteBudget_ShouldReturnNoContent() {

        ResponseEntity<ApiResponseDto<Void>> response =
                budgetController.deleteBudget("budget-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(budgetService).deleteBudget("budget-1");
    }
}