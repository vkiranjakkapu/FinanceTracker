
package com.financetracker.finance.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;

import com.financetracker.finance.domain.model.Budget;
import com.financetracker.finance.domain.model.Transaction;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.response.DashboardResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.BudgetRepository;
import com.financetracker.finance.services.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

	private static final UUID USER_ID = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

	private static final YearMonth SELECTED_MONTH = YearMonth.of(2026, 11);

	private static final LocalDate MONTH_START = SELECTED_MONTH.atDay(1);

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private BudgetRepository budgetRepository;

	@Mock
	private CurrentUserService currentUserService;

	@InjectMocks
	private DashboardServiceImpl dashboardService;

	@Test
	void getDashboard_ShouldReturnCompleteDashboard() {

		stubAggregations(
				List.of(
						new Document("_id", "INCOME")
								.append("total", decimal("10000.00")),
						new Document("_id", "EXPENSE")
								.append("total", decimal("6000.00"))),
				List.of(
						new Document(
								"_id",
								new Document("categoryId", "category-1")
										.append("categoryName", "Food"))
								.append("total", decimal("6000.00"))));

		Transaction juneIncome = transaction(
				"transaction-1",
				TransactionType.INCOME,
				"1000.00",
				LocalDate.of(2026, 6, 15),
				"Salary");

		Transaction novemberIncome = transaction(
				"transaction-2",
				TransactionType.INCOME,
				"10000.00",
				LocalDate.of(2026, 11, 2),
				"Salary");

		Transaction novemberExpense = transaction(
				"transaction-3",
				TransactionType.EXPENSE,
				"6000.00",
				LocalDate.of(2026, 11, 5),
				"Food");

		when(currentUserService.userId()).thenReturn(USER_ID);

		when(mongoTemplate.find(any(Query.class), eq(Transaction.class)))
				.thenReturn(List.of(juneIncome, novemberIncome, novemberExpense))
				.thenReturn(List.of(novemberExpense));

		Budget budget = Budget.builder()
				.id("budget-1")
				.ownerId(USER_ID.toString())
				.categoryId("category-1")
				.categoryName("Food")
				.month(MONTH_START)
				.amount(new BigDecimal("5000.00"))
				.build();

		when(budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						USER_ID.toString(), MONTH_START))
				.thenReturn(List.of(budget));

		DashboardResponse response = dashboardService.getDashboard(SELECTED_MONTH);

		// Summary
		assertEquals(MONTH_START, response.month());
		assertEquals(new BigDecimal("10000.00"), response.totalIncome());
		assertEquals(new BigDecimal("6000.00"), response.totalExpenses());
		assertEquals(new BigDecimal("4000.00"), response.balance());

		// Category expenses
		assertEquals(1, response.categoryExpenses().size());
		assertEquals(
				"category-1",
				response.categoryExpenses().get(0).categoryId());
		assertEquals(
				"Food",
				response.categoryExpenses().get(0).categoryName());
		assertEquals(
				new BigDecimal("6000.00"),
				response.categoryExpenses().get(0).amount());

		// Monthly trends
		assertEquals(6, response.monthlyTrends().size());

		assertEquals(
				YearMonth.of(2026, 6),
				response.monthlyTrends().get(0).month());

		assertEquals(
				new BigDecimal("1000.00"),
				response.monthlyTrends().get(0).income());

		assertEquals(
				SELECTED_MONTH,
				response.monthlyTrends().get(5).month());

		assertEquals(
				new BigDecimal("10000.00"),
				response.monthlyTrends().get(5).income());

		assertEquals(
				new BigDecimal("6000.00"),
				response.monthlyTrends().get(5).expenses());

		// Recent transactions
		assertEquals(1, response.recentTransactions().size());
		assertEquals(
				"transaction-3",
				response.recentTransactions().get(0).id());
		assertEquals(
				TransactionType.EXPENSE,
				response.recentTransactions().get(0).type());

		// Budget utilization
		assertEquals(1, response.budgetUtilization().size());

		assertEquals(
				new BigDecimal("5000.00"),
				response.budgetUtilization().get(0).budgetAmount());

		assertEquals(
				new BigDecimal("6000.00"),
				response.budgetUtilization().get(0).spentAmount());

		assertEquals(
				new BigDecimal("-1000.00"),
				response.budgetUtilization().get(0).remainingAmount());

		assertEquals(
				new BigDecimal("120.00"),
				response.budgetUtilization().get(0)
						.utilizationPercentage());

		verify(budgetRepository)
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						USER_ID.toString(), MONTH_START);
	}

	@Test
	void getDashboard_ShouldReturnZeroTotalsWhenNoTransactionsExist() {

		stubAggregations(List.of(), List.of());

		when(currentUserService.userId()).thenReturn(USER_ID);

		when(mongoTemplate.find(any(Query.class), eq(Transaction.class)))
				.thenReturn(List.of())
				.thenReturn(List.of());

		when(budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						USER_ID.toString(), MONTH_START))
				.thenReturn(List.of());

		DashboardResponse response = dashboardService.getDashboard(SELECTED_MONTH);

		assertEquals(BigDecimal.ZERO, response.totalIncome());
		assertEquals(BigDecimal.ZERO, response.totalExpenses());
		assertEquals(BigDecimal.ZERO, response.balance());

		assertTrue(response.categoryExpenses().isEmpty());
		assertTrue(response.recentTransactions().isEmpty());
		assertTrue(response.budgetUtilization().isEmpty());

		assertEquals(6, response.monthlyTrends().size());

		response.monthlyTrends().forEach(trend -> {
			assertEquals(BigDecimal.ZERO, trend.income());
			assertEquals(BigDecimal.ZERO, trend.expenses());
		});
	}

	@Test
	void getDashboard_ShouldRejectNullMonth() {

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> dashboardService.getDashboard(null));

		assertEquals(
				BusinessErrors.INVALID_REQUEST.name(),
				exception.getDefinition().getErrorName());

		verifyNoInteractions(
				mongoTemplate,
				budgetRepository,
				currentUserService);
	}

	@Test
	void getDashboard_ShouldCalculateBalanceWhenOnlyIncomeExists() {

		stubAggregations(
				List.of(
						new Document("_id", "INCOME")
								.append("total", decimal("25000.00"))),
				List.of());

		when(currentUserService.userId()).thenReturn(USER_ID);

		when(mongoTemplate.find(any(Query.class),
				eq(Transaction.class)))
				.thenReturn(List.of())
				.thenReturn(List.of());

		when(budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						USER_ID.toString(), MONTH_START))
				.thenReturn(List.of());

		DashboardResponse response = dashboardService.getDashboard(SELECTED_MONTH);

		assertEquals(new BigDecimal("25000.00"), response.totalIncome());
		assertEquals(BigDecimal.ZERO, response.totalExpenses());
		assertEquals(new BigDecimal("25000.00"), response.balance());
	}

	@Test
	void getDashboard_ShouldCalculateNegativeBalanceWhenExpensesExceedIncome() {

		stubAggregations(
				List.of(
						new Document("_id", "INCOME")
								.append("total", decimal("2000.00")),
						new Document("_id", "EXPENSE")
								.append("total", decimal("5000.00"))),
				List.of());

		when(currentUserService.userId()).thenReturn(USER_ID);

		when(mongoTemplate.find(any(
				org.springframework.data.mongodb.core.query.Query.class),
				eq(Transaction.class)))
				.thenReturn(List.of())
				.thenReturn(List.of());

		when(budgetRepository
				.findAllByOwnerIdAndMonthOrderByCategoryNameAsc(
						USER_ID.toString(), MONTH_START))
				.thenReturn(List.of());

		DashboardResponse response = dashboardService.getDashboard(SELECTED_MONTH);

		assertEquals(new BigDecimal("2000.00"), response.totalIncome());
		assertEquals(new BigDecimal("5000.00"), response.totalExpenses());
		assertEquals(new BigDecimal("-3000.00"), response.balance());
	}

	private void stubAggregations(
			List<Document> totals,
			List<Document> categoryExpenses) {

		@SuppressWarnings("unchecked")
		AggregationResults<Document> totalsResults = mock(AggregationResults.class);

		@SuppressWarnings("unchecked")
		AggregationResults<Document> categoryResults = mock(AggregationResults.class);

		when(totalsResults.getMappedResults()).thenReturn(totals);
		when(categoryResults.getMappedResults())
				.thenReturn(categoryExpenses);

		when(mongoTemplate.aggregate(
				any(TypedAggregation.class),
				eq(Document.class)))
				.thenReturn(totalsResults)
				.thenReturn(categoryResults);
	}

	private Decimal128 decimal(String value) {
		return new Decimal128(new BigDecimal(value));
	}

	private Transaction transaction(
			String id,
			TransactionType type,
			String amount,
			LocalDate date,
			String categoryName) {

		return Transaction.builder()
				.id(id)
				.ownerId(USER_ID.toString())
				.type(type)
				.amount(new BigDecimal(amount))
				.description(categoryName + " transaction")
				.categoryId("category-1")
				.categoryName(categoryName)
				.transactionDate(date)
				.notes("Test notes")
				.createdAt(Instant.parse("2026-11-05T10:00:00Z"))
				.updatedAt(Instant.parse("2026-11-05T10:00:00Z"))
				.build();
	}
}
