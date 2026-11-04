package com.financetracker.finance.services.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.financetracker.finance.domain.model.Budget;
import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateBudgetRequest;
import com.financetracker.finance.dto.response.BudgetResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.BudgetRepository;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.services.BudgetService;
import com.financetracker.finance.services.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

	private final BudgetRepository budgetRepository;
	private final CategoryRepository categoryRepository;
	private final CurrentUserService currentUser;

	@Override
	public BudgetResponse createBudget(CreateBudgetRequest request) {
		String ownerId = getCurrentOwnerId();

		validateMonth(request.month());

		Category category = validateCategory(request.categoryId());

		boolean alreadyExists = budgetRepository
				.findByOwnerIdAndCategoryIdAndMonth(
						ownerId, category.getId(), request.month())
				.isPresent();

		if (alreadyExists) {
			throw new BusinessException(BusinessErrors.DUPLICATE_RESOURCE,
					"A budget already exists for this category and month.");
		}

		Instant now = Instant.now();

		Budget budget = Budget.builder()
				.ownerId(ownerId)
				.categoryId(category.getId())
				.categoryName(category.getName())
				.month(request.month())
				.amount(request.amount())
				.createdAt(now)
				.updatedAt(now)
				.build();

		return toResponse(budgetRepository.save(budget));
	}

	@Override
	public List<BudgetResponse> getBudgets(LocalDate month) {
		String ownerId = getCurrentOwnerId();

		validateMonth(month);

		return budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						ownerId, month)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public BudgetResponse updateBudget(
			String budgetId,
			CreateBudgetRequest request) {

		String ownerId = getCurrentOwnerId();

		validateMonth(request.month());

		Budget budget = budgetRepository
				.findByIdAndOwnerId(budgetId, ownerId)
				.orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Budget not found."));

		Category category = validateCategory(request.categoryId());

		budgetRepository
				.findByOwnerIdAndCategoryIdAndMonth(
						ownerId, category.getId(), request.month())
				.filter(existing -> !existing.getId().equals(budgetId))
				.ifPresent(existing -> {
					throw new BusinessException(BusinessErrors.DUPLICATE_RESOURCE,
							"A budget already exists for this category and month.");
				});

		budget.setCategoryId(category.getId());
		budget.setCategoryName(category.getName());
		budget.setMonth(request.month());
		budget.setAmount(request.amount());
		budget.setUpdatedAt(Instant.now());

		return toResponse(budgetRepository.save(budget));
	}

	@Override
	public void deleteBudget(String budgetId) {
		String ownerId = getCurrentOwnerId();

		Budget budget = budgetRepository
				.findByIdAndOwnerId(budgetId, ownerId)
				.orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Budget not found."));

		budgetRepository.delete(budget);
	}

	private Category validateCategory(String categoryId) {

		Category category = categoryRepository
				.findByIdAndOwnerId(categoryId, getCurrentOwnerId())
				.or(() -> categoryRepository.findByIdAndIsDefaultTrue(categoryId))
				.orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Category not found."));

		if (category.getType() != TransactionType.EXPENSE) {
			throw new BusinessException(BusinessErrors.BUDGET_CATEGORY_MUST_BE_EXPENSE,
					"Budgets can only be created for expense categories.");
		}

		return category;
	}

	private void validateMonth(LocalDate month) {
		if (month == null || month.getDayOfMonth() != 1) {
			throw new BusinessException(BusinessErrors.INVALID_BUDGET_MONTH,
					"Budget month must be the first day of the month.");
		}
	}

	private String getCurrentOwnerId() {
		return currentUser.userId().toString();
	}

	private BudgetResponse toResponse(Budget budget) {
		return new BudgetResponse(
				budget.getId(),
				budget.getCategoryId(),
				budget.getCategoryName(),
				budget.getMonth(),
				budget.getAmount(),
				budget.getCreatedAt(),
				budget.getUpdatedAt());
	}
}