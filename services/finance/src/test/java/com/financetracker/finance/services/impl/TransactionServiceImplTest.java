
package com.financetracker.finance.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.Transaction;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateTransactionRequest;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.repository.TransactionRepository;
import com.financetracker.finance.services.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

	private static final UUID USER_ID = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

	private static final String CATEGORY_ID = "category-1";
	private static final String TRANSACTION_ID = "transaction-1";

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private CurrentUserService currentUser;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	private Category expenseCategory;

	private CreateTransactionRequest request;

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

		request = new CreateTransactionRequest(
				TransactionType.EXPENSE,
				new BigDecimal("500.00"),
				"  Lunch  ",
				CATEGORY_ID,
				LocalDate.of(2026, 11, 5),
				"Lunch expense");
	}

	@Test
	void createTransaction_ShouldSaveTransaction() {

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.of(expenseCategory));

		when(transactionRepository.save(any(Transaction.class)))
				.thenAnswer(invocation -> {
					Transaction transaction = invocation.getArgument(0);
					transaction.setId(TRANSACTION_ID);
					return transaction;
				});

		TransactionResponse response = transactionService.createTransaction(request);

		assertNotNull(response);
		assertEquals(TRANSACTION_ID, response.id());
		assertEquals(TransactionType.EXPENSE, response.type());
		assertEquals(new BigDecimal("500.00"), response.amount());
		assertEquals("Lunch", response.description());
		assertEquals(CATEGORY_ID, response.categoryId());
		assertEquals("Food", response.categoryName());
		assertEquals(LocalDate.of(2026, 11, 5),
				response.transactionDate());
		assertNotNull(response.createdAt());
		assertNotNull(response.updatedAt());

		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	void createTransaction_ShouldRejectMismatchedCategoryType() {

		Category incomeCategory = Category.builder()
				.id(CATEGORY_ID)
				.ownerId(USER_ID.toString())
				.name("Salary")
				.type(TransactionType.INCOME)
				.build();

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.of(incomeCategory));

		assertThrows(
				BusinessException.class,
				() -> transactionService.createTransaction(request));

		verify(transactionRepository, never())
				.save(any(Transaction.class));
	}

	@Test
	void getTransaction_ShouldReturnTransaction() {

		Transaction transaction = Transaction.builder()
				.id(TRANSACTION_ID)
				.ownerId(USER_ID.toString())
				.type(TransactionType.EXPENSE)
				.amount(new BigDecimal("500.00"))
				.description("Lunch")
				.categoryId(CATEGORY_ID)
				.categoryName("Food")
				.transactionDate(LocalDate.of(2026, 11, 5))
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		when(transactionRepository.findByIdAndOwnerId(
				TRANSACTION_ID, USER_ID.toString()))
				.thenReturn(Optional.of(transaction));

		TransactionResponse response = transactionService.getTransaction(TRANSACTION_ID);

		assertNotNull(response);
		assertEquals(TRANSACTION_ID, response.id());
		assertEquals("Lunch", response.description());
		assertEquals(new BigDecimal("500.00"), response.amount());
	}

	@Test
	void getTransaction_ShouldThrowWhenNotFound() {

		when(transactionRepository.findByIdAndOwnerId(
				TRANSACTION_ID, USER_ID.toString()))
				.thenReturn(Optional.empty());

		assertThrows(
				BusinessException.class,
				() -> transactionService.getTransaction(TRANSACTION_ID));
	}

	@Test
	void updateTransaction_ShouldUpdateExistingTransaction() {

		Transaction transaction = Transaction.builder()
				.id(TRANSACTION_ID)
				.ownerId(USER_ID.toString())
				.type(TransactionType.EXPENSE)
				.amount(new BigDecimal("200.00"))
				.description("Old description")
				.categoryId(CATEGORY_ID)
				.categoryName("Food")
				.transactionDate(LocalDate.of(2026, 11, 1))
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		when(transactionRepository.findByIdAndOwnerId(
				TRANSACTION_ID, USER_ID.toString()))
				.thenReturn(Optional.of(transaction));

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.of(expenseCategory));

		when(transactionRepository.save(any(Transaction.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreateTransactionRequest updateRequest = new CreateTransactionRequest(
				TransactionType.EXPENSE,
				new BigDecimal("750.00"),
				"Updated lunch",
				CATEGORY_ID,
				LocalDate.of(2026, 11, 5),
				"Updated notes");

		TransactionResponse response = transactionService.updateTransaction(
				TRANSACTION_ID, updateRequest);

		assertEquals(new BigDecimal("750.00"), response.amount());
		assertEquals("Updated lunch", response.description());
		assertEquals("Updated notes", response.notes());
		assertEquals(LocalDate.of(2026, 11, 5),
				response.transactionDate());

		verify(transactionRepository).save(transaction);
	}

	@Test
	void deleteTransaction_ShouldDeleteExistingTransaction() {

		Transaction transaction = Transaction.builder()
				.id(TRANSACTION_ID)
				.ownerId(USER_ID.toString())
				.build();

		when(transactionRepository.findByIdAndOwnerId(
				TRANSACTION_ID, USER_ID.toString()))
				.thenReturn(Optional.of(transaction));

		transactionService.deleteTransaction(TRANSACTION_ID);

		verify(transactionRepository).delete(transaction);
	}

	@Test
	void deleteTransaction_ShouldThrowWhenTransactionNotOwnedByUser() {

		when(transactionRepository.findByIdAndOwnerId(
				TRANSACTION_ID, USER_ID.toString()))
				.thenReturn(Optional.empty());

		assertThrows(
				BusinessException.class,
				() -> transactionService.deleteTransaction(TRANSACTION_ID));

		verify(transactionRepository, never()).delete(any(Transaction.class));
	}

	@Test
	void getTransactions_ShouldRejectInvalidDateRange() {

		LocalDate fromDate = LocalDate.of(2026, 11, 10);
		LocalDate toDate = LocalDate.of(2026, 11, 1);

		assertThrows(
				BusinessException.class,
				() -> transactionService.getTransactions(
						null, null, fromDate, toDate));

		verify(mongoTemplate, never())
				.find(any(Query.class), eq(Transaction.class));
	}
}
