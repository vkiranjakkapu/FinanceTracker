package com.financetracker.finance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.finance.domain.model.Transaction;
import com.financetracker.finance.domain.model.TransactionType;

public interface TransactionRepository
		extends MongoRepository<Transaction, String> {

	List<Transaction> findAllByOwnerIdOrderByTransactionDateDesc(
			String ownerId);

	Optional<Transaction> findByIdAndOwnerId(
			String id, String ownerId);

	List<Transaction> findAllByOwnerIdAndTypeOrderByTransactionDateDesc(
			String ownerId, TransactionType type);

	List<Transaction> findAllByOwnerIdAndTransactionDateBetweenOrderByTransactionDateDesc(
			String ownerId, LocalDate startDate, LocalDate endDate);

	List<Transaction> findAllByOwnerIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDesc(
			String ownerId,
			TransactionType type,
			LocalDate startDate,
			LocalDate endDate);

	List<Transaction> findAllByOwnerIdAndCategoryIdOrderByTransactionDateDesc(
			String ownerId, String categoryId);
}