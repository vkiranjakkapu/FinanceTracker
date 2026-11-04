package com.financetracker.finance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.finance.domain.model.Budget;

public interface BudgetRepository
		extends MongoRepository<Budget, String> {

	List<Budget> findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
			String ownerId, LocalDate month);

	Optional<Budget> findByOwnerIdAndCategoryIdAndMonth(
			String ownerId, String categoryId, LocalDate month);

	Optional<Budget> findByIdAndOwnerId(
			String id, String ownerId);
}