package com.financetracker.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;

public interface CategoryRepository
		extends MongoRepository<Category, String> {

	List<Category> findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
			String ownerId);

	List<Category> findAllByOwnerIdAndTypeOrderByNameAsc(
			String ownerId, TransactionType type);

	List<Category> findAllByIsDefaultTrueAndTypeOrderByNameAsc(
			TransactionType type);

	Optional<Category> findByIdAndOwnerId(
			String id, String ownerId);

	Optional<Category> findByIdAndIsDefaultTrue(String id);

	Optional<Category> findByOwnerIdAndNameIgnoreCaseAndType(
			String ownerId, String name, TransactionType type);

	Optional<Category> findByNameIgnoreCaseAndTypeAndIsDefaultTrue(
			String name, TransactionType type);
}