
package com.financetracker.finance.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financetracker.finance.domain.model.Budget;
import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateBudgetRequest;
import com.financetracker.finance.dto.response.BudgetResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.BudgetRepository;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.services.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    private static final UUID USER_ID =
            UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

    private static final String BUDGET_ID = "budget-1";
    private static final String CATEGORY_ID = "category-1";

    private static final LocalDate MONTH =
            LocalDate.of(2026, 11, 1);

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrentUserService currentUser;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private Category expenseCategory;
    private CreateBudgetRequest request;

    @BeforeEach
    void setUp() {

        when(currentUser.userId()).thenReturn(USER_ID);

        expenseCategory = Category.builder()
                .id(CATEGORY_ID)
                .ownerId(USER_ID.toString())
                .name("Food")
                .type(TransactionType.EXPENSE)
                .isDefault(false)
                .build();

        request = new CreateBudgetRequest(
                CATEGORY_ID,
                MONTH,
                new BigDecimal("5000.00"));
    }

    @Test
    void createBudget_ShouldSaveBudget() {

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.of(expenseCategory));

        when(budgetRepository.findByOwnerIdAndCategoryIdAndMonth(
                USER_ID.toString(), CATEGORY_ID, MONTH))
                .thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> {
                    Budget budget = invocation.getArgument(0);
                    budget.setId(BUDGET_ID);
                    return budget;
                });

        BudgetResponse response =
                budgetService.createBudget(request);

        assertNotNull(response);
        assertEquals(BUDGET_ID, response.id());
        assertEquals(CATEGORY_ID, response.categoryId());
        assertEquals("Food", response.categoryName());
        assertEquals(MONTH, response.month());
        assertEquals(new BigDecimal("5000.00"), response.amount());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void createBudget_ShouldRejectDuplicateBudget() {

        Budget existingBudget = Budget.builder()
                .id(BUDGET_ID)
                .ownerId(USER_ID.toString())
                .categoryId(CATEGORY_ID)
                .month(MONTH)
                .amount(new BigDecimal("3000.00"))
                .build();

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.of(expenseCategory));

        when(budgetRepository.findByOwnerIdAndCategoryIdAndMonth(
                USER_ID.toString(), CATEGORY_ID, MONTH))
                .thenReturn(Optional.of(existingBudget));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(request));

        assertEquals(
                BusinessErrors.DUPLICATE_RESOURCE.name(),
                exception.getDefinition().getErrorName());

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void createBudget_ShouldRejectInvalidMonth() {

        CreateBudgetRequest invalidRequest =
                new CreateBudgetRequest(
                        CATEGORY_ID,
                        LocalDate.of(2026, 11, 5),
                        new BigDecimal("5000.00"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(invalidRequest));

        assertEquals(
                BusinessErrors.INVALID_BUDGET_MONTH.name(),
                exception.getDefinition().getErrorName());

        verify(categoryRepository, never())
                .findByIdAndOwnerId(anyString(), anyString());

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void createBudget_ShouldRejectIncomeCategory() {

        Category incomeCategory = Category.builder()
                .id(CATEGORY_ID)
                .ownerId(USER_ID.toString())
                .name("Salary")
                .type(TransactionType.INCOME)
                .build();

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.of(incomeCategory));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(request));

        assertEquals(
                BusinessErrors.BUDGET_CATEGORY_MUST_BE_EXPENSE.name(),
                exception.getDefinition().getErrorName());

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void createBudget_ShouldAllowDefaultExpenseCategory() {

        Category defaultCategory = Category.builder()
                .id(CATEGORY_ID)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .isDefault(true)
                .build();

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndIsDefaultTrue(CATEGORY_ID))
                .thenReturn(Optional.of(defaultCategory));

        when(budgetRepository.findByOwnerIdAndCategoryIdAndMonth(
                USER_ID.toString(), CATEGORY_ID, MONTH))
                .thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> {
                    Budget budget = invocation.getArgument(0);
                    budget.setId(BUDGET_ID);
                    return budget;
                });

        BudgetResponse response =
                budgetService.createBudget(request);

        assertEquals(CATEGORY_ID, response.categoryId());
        assertEquals("Food", response.categoryName());

        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void getBudgets_ShouldReturnBudgetsForMonth() {

        Budget budget = Budget.builder()
                .id(BUDGET_ID)
                .ownerId(USER_ID.toString())
                .categoryId(CATEGORY_ID)
                .categoryName("Food")
                .month(MONTH)
                .amount(new BigDecimal("5000.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(budgetRepository
                .findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
                        USER_ID.toString(), MONTH))
                .thenReturn(List.of(budget));

        List<BudgetResponse> responses =
                budgetService.getBudgets(MONTH);

        assertEquals(1, responses.size());
        assertEquals(BUDGET_ID, responses.get(0).id());
        assertEquals("Food", responses.get(0).categoryName());
        assertEquals(new BigDecimal("5000.00"),
                responses.get(0).amount());

        verify(budgetRepository)
                .findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
                        USER_ID.toString(), MONTH);
    }

    @Test
    void updateBudget_ShouldUpdateExistingBudget() {

        Budget existingBudget = Budget.builder()
                .id(BUDGET_ID)
                .ownerId(USER_ID.toString())
                .categoryId(CATEGORY_ID)
                .categoryName("Food")
                .month(MONTH)
                .amount(new BigDecimal("3000.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        CreateBudgetRequest updateRequest =
                new CreateBudgetRequest(
                        CATEGORY_ID,
                        MONTH,
                        new BigDecimal("7500.00"));

        when(budgetRepository.findByIdAndOwnerId(
                BUDGET_ID, USER_ID.toString()))
                .thenReturn(Optional.of(existingBudget));

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.of(expenseCategory));

        when(budgetRepository.findByOwnerIdAndCategoryIdAndMonth(
                USER_ID.toString(), CATEGORY_ID, MONTH))
                .thenReturn(Optional.of(existingBudget));

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BudgetResponse response =
                budgetService.updateBudget(BUDGET_ID, updateRequest);

        assertEquals(BUDGET_ID, response.id());
        assertEquals(new BigDecimal("7500.00"), response.amount());
        assertEquals(CATEGORY_ID, response.categoryId());
        assertEquals(MONTH, response.month());

        verify(budgetRepository).save(existingBudget);
    }

    @Test
    void updateBudget_ShouldRejectDuplicateBudget() {

        Budget existingBudget = Budget.builder()
                .id(BUDGET_ID)
                .ownerId(USER_ID.toString())
                .categoryId(CATEGORY_ID)
                .month(MONTH)
                .build();

        Budget anotherBudget = Budget.builder()
                .id("budget-2")
                .ownerId(USER_ID.toString())
                .categoryId(CATEGORY_ID)
                .month(MONTH)
                .build();

        when(budgetRepository.findByIdAndOwnerId(
                BUDGET_ID, USER_ID.toString()))
                .thenReturn(Optional.of(existingBudget));

        when(categoryRepository.findByIdAndOwnerId(
                CATEGORY_ID, USER_ID.toString()))
                .thenReturn(Optional.of(expenseCategory));

        when(budgetRepository.findByOwnerIdAndCategoryIdAndMonth(
                USER_ID.toString(), CATEGORY_ID, MONTH))
                .thenReturn(Optional.of(anotherBudget));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.updateBudget(BUDGET_ID, request));

        assertEquals(
                BusinessErrors.DUPLICATE_RESOURCE.name(),
                exception.getDefinition().getErrorName());

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void updateBudget_ShouldThrowWhenBudgetNotOwnedByUser() {

        when(budgetRepository.findByIdAndOwnerId(
                BUDGET_ID, USER_ID.toString()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.updateBudget(BUDGET_ID, request));

        assertEquals(
                BusinessErrors.RESOURCE_NOT_FOUND.name(),
                exception.getDefinition().getErrorName());

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void deleteBudget_ShouldDeleteExistingBudget() {

        Budget budget = Budget.builder()
                .id(BUDGET_ID)
                .ownerId(USER_ID.toString())
                .build();

        when(budgetRepository.findByIdAndOwnerId(
                BUDGET_ID, USER_ID.toString()))
                .thenReturn(Optional.of(budget));

        budgetService.deleteBudget(BUDGET_ID);

        verify(budgetRepository).delete(budget);
    }

    @Test
    void deleteBudget_ShouldRejectBudgetNotOwnedByUser() {

        when(budgetRepository.findByIdAndOwnerId(
                BUDGET_ID, USER_ID.toString()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.deleteBudget(BUDGET_ID));

        assertEquals(
                BusinessErrors.RESOURCE_NOT_FOUND.name(),
                exception.getDefinition().getErrorName());

        verify(budgetRepository, never())
                .delete(any(Budget.class));
    }
}
