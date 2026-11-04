
package com.financetracker.finance.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.data.core.TypedPropertyPath;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.financetracker.finance.domain.model.Budget;
import com.financetracker.finance.domain.model.Transaction;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.response.BudgetUtilizationResponse;
import com.financetracker.finance.dto.response.CategoryExpenseResponse;
import com.financetracker.finance.dto.response.DashboardResponse;
import com.financetracker.finance.dto.response.MonthlyTrendResponse;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.BudgetRepository;
import com.financetracker.finance.services.CurrentUserService;
import com.financetracker.finance.services.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final int RECENT_TRANSACTION_LIMIT = 5;
	private static final int TREND_MONTHS = 6;

	private final MongoTemplate mongoTemplate;
	private final BudgetRepository budgetRepository;
	private final CurrentUserService currentUserService;

	@Override
	public DashboardResponse getDashboard(YearMonth requestedMonth) {

		if (requestedMonth == null) {
			throw new BusinessException(
					BusinessErrors.INVALID_REQUEST,
					"Dashboard month is required.");
		}

		String ownerId = currentUserService.userId().toString();

		YearMonth selectedMonth = requestedMonth;

		LocalDate monthStart = selectedMonth.atDay(1);
		LocalDate monthEnd = selectedMonth.atEndOfMonth();

		Map<String, BigDecimal> totals = getTransactionTotals(ownerId, monthStart, monthEnd);

		BigDecimal totalIncome = totals.getOrDefault(
				TransactionType.INCOME.name(), ZERO);

		BigDecimal totalExpenses = totals.getOrDefault(
				TransactionType.EXPENSE.name(), ZERO);

		BigDecimal balance = totalIncome.subtract(totalExpenses);

		List<CategoryExpenseResponse> categoryExpenses = getCategoryExpenses(ownerId, monthStart, monthEnd);

		List<MonthlyTrendResponse> monthlyTrends = getMonthlyTrends(ownerId, selectedMonth);

		List<TransactionResponse> recentTransactions = getRecentTransactions(ownerId, monthStart, monthEnd);

		List<BudgetUtilizationResponse> budgetUtilization = getBudgetUtilization(
				ownerId, selectedMonth, categoryExpenses);

		return new DashboardResponse(
				monthStart,
				totalIncome,
				totalExpenses,
				balance,
				categoryExpenses,
				monthlyTrends,
				recentTransactions,
				budgetUtilization);
	}

	private Map<String, BigDecimal> getTransactionTotals(
			String ownerId,
			LocalDate startDate,
			LocalDate endDate) {

		Criteria criteria = Criteria.where(
				TypedPropertyPath.path(Transaction::getOwnerId))
				.is(ownerId)
				.and(TypedPropertyPath.path(Transaction::getTransactionDate))
				.gte(startDate)
				.lte(endDate);

		TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(
				Transaction.class,
				Aggregation.match(criteria),
				Aggregation.group("type")
						.sum("amount").as("total"));

		List<Document> results = mongoTemplate.aggregate(
				aggregation,
				Document.class).getMappedResults();

		Map<String, BigDecimal> totals = new HashMap<>();

		for (Document result : results) {
			totals.put(
					result.getString("_id"),
					toBigDecimal(result.get("total")));
		}

		return totals;
	}

	private List<CategoryExpenseResponse> getCategoryExpenses(
			String ownerId,
			LocalDate startDate,
			LocalDate endDate) {

		Criteria criteria = Criteria.where(
				TypedPropertyPath.path(Transaction::getOwnerId))
				.is(ownerId)
				.and(TypedPropertyPath.path(Transaction::getType))
				.is(TransactionType.EXPENSE)
				.and(TypedPropertyPath.path(Transaction::getTransactionDate))
				.gte(startDate)
				.lte(endDate);

		TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(
				Transaction.class,
				Aggregation.match(criteria),
				Aggregation.group("categoryId", "categoryName")
						.sum("amount").as("total"),
				Aggregation.sort(Sort.Direction.DESC, "total"));

		List<Document> results = mongoTemplate.aggregate(
				aggregation,
				Document.class).getMappedResults();

		return results.stream()
				.map(result -> {
					Document id = result.get("_id", Document.class);

					return new CategoryExpenseResponse(
							id.getString("categoryId"),
							id.getString("categoryName"),
							toBigDecimal(result.get("total")));
				})
				.toList();
	}

	private List<MonthlyTrendResponse> getMonthlyTrends(
			String ownerId,
			YearMonth selectedMonth) {

		YearMonth firstMonth = selectedMonth.minusMonths(
				TREND_MONTHS - 1);

		LocalDate startDate = firstMonth.atDay(1);
		LocalDate endDate = selectedMonth.atEndOfMonth();

		Criteria criteria = Criteria.where(
				TypedPropertyPath.path(Transaction::getOwnerId))
				.is(ownerId)
				.and(TypedPropertyPath.path(Transaction::getTransactionDate))
				.gte(startDate)
				.lte(endDate);

		Query query = new Query();
		query.addCriteria(criteria);

		List<Transaction> transactions = mongoTemplate.find(
				query, Transaction.class);

		Map<YearMonth, BigDecimal[]> monthlyTotals = new HashMap<>();

		for (int i = 0; i < TREND_MONTHS; i++) {
			YearMonth month = firstMonth.plusMonths(i);

			monthlyTotals.put(
					month,
					new BigDecimal[] { ZERO, ZERO });
		}

		for (Transaction transaction : transactions) {
			YearMonth transactionMonth = YearMonth.from(
					transaction.getTransactionDate());

			BigDecimal[] totals = monthlyTotals.get(transactionMonth);

			if (totals == null) {
				continue;
			}

			if (transaction.getType() == TransactionType.INCOME) {
				totals[0] = totals[0].add(transaction.getAmount());
			} else if (transaction.getType() == TransactionType.EXPENSE) {
				totals[1] = totals[1].add(transaction.getAmount());
			}
		}

		List<MonthlyTrendResponse> trends = new ArrayList<>();

		monthlyTotals.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEach(entry -> trends.add(
						new MonthlyTrendResponse(
								entry.getKey(),
								entry.getValue()[0],
								entry.getValue()[1])));

		return trends;
	}

	private List<TransactionResponse> getRecentTransactions(
			String ownerId,
			LocalDate startDate,
			LocalDate endDate) {

		Criteria criteria = Criteria.where(
				TypedPropertyPath.path(Transaction::getOwnerId))
				.is(ownerId)
				.and(TypedPropertyPath.path(Transaction::getTransactionDate))
				.gte(startDate)
				.lte(endDate);

		Query query = new Query();
		query.addCriteria(criteria);

		query.with(
				Sort.by(
						Sort.Direction.DESC,
						TypedPropertyPath.path(Transaction::getTransactionDate),
						TypedPropertyPath.path(Transaction::getCreatedAt)));

		query.limit(RECENT_TRANSACTION_LIMIT);

		return mongoTemplate.find(query, Transaction.class)
				.stream()
				.map(this::toTransactionResponse)
				.toList();
	}

	private List<BudgetUtilizationResponse> getBudgetUtilization(
			String ownerId,
			YearMonth selectedMonth,
			List<CategoryExpenseResponse> categoryExpenses) {

		LocalDate monthStart = selectedMonth.atDay(1);

		Map<String, BigDecimal> spendingByCategory = new HashMap<>();

		categoryExpenses.forEach(category -> spendingByCategory.put(
				category.categoryId(),
				category.amount()));

		List<Budget> budgets = budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						ownerId, monthStart);

		return budgets.stream()
				.map(budget -> {
					BigDecimal spent = spendingByCategory.getOrDefault(
							budget.getCategoryId(), ZERO);

					BigDecimal remaining = budget.getAmount().subtract(spent);

					BigDecimal percentage = spent
							.multiply(BigDecimal.valueOf(100))
							.divide(
									budget.getAmount(),
									2,
									RoundingMode.HALF_UP);

					return new BudgetUtilizationResponse(
							budget.getCategoryId(),
							budget.getCategoryName(),
							budget.getAmount(),
							spent,
							remaining,
							percentage);
				})
				.toList();
	}

	private TransactionResponse toTransactionResponse(
			Transaction transaction) {

		return new TransactionResponse(
				transaction.getId(),
				transaction.getType(),
				transaction.getAmount(),
				transaction.getDescription(),
				transaction.getCategoryId(),
				transaction.getCategoryName(),
				transaction.getTransactionDate(),
				transaction.getNotes(),
				transaction.getCreatedAt(),
				transaction.getUpdatedAt());
	}

	private BigDecimal toBigDecimal(Object value) {

		if (value == null) {
			return ZERO;
		}

		if (value instanceof Decimal128 decimal128) {
			return decimal128.bigDecimalValue();
		}

		if (value instanceof BigDecimal decimal) {
			return decimal;
		}

		if (value instanceof Number number) {
			return new BigDecimal(number.toString());
		}

		throw new BusinessException(
				"Unexpected monetary aggregation result: "
						+ value.getClass().getName());
	}
}
