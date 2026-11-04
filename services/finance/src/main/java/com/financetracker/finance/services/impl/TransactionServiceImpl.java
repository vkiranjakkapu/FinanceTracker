package com.financetracker.finance.services.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.core.TypedPropertyPath;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.Transaction;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateTransactionRequest;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.repository.TransactionRepository;
import com.financetracker.finance.services.CurrentUserService;
import com.financetracker.finance.services.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final MongoTemplate mongoTemplate;
    private final CurrentUserService currentUser;

    @Override
    public TransactionResponse createTransaction(
            CreateTransactionRequest request) {

        String ownerId = getCurrentOwnerId();
        Category category = validateCategory(
                request.categoryId(), request.type());

        Instant now = Instant.now();

        Transaction transaction = Transaction.builder()
                .ownerId(ownerId)
                .type(request.type())
                .amount(request.amount())
                .description(request.description().trim())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .transactionDate(request.transactionDate())
                .notes(request.notes())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionResponse> getTransactions(
            TransactionType type,
            String categoryId,
            LocalDate fromDate,
            LocalDate toDate) {

        String ownerId = getCurrentOwnerId();

        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {
            throw new BusinessException(BusinessErrors.INVALID_DATE_RANGE,
                    "From date cannot be after to date.");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where(TypedPropertyPath.path(Transaction::getOwnerId)).is(ownerId));

        if (type != null) {
            query.addCriteria(Criteria.where(TypedPropertyPath.path(Transaction::getType)).is(type));
        }

        if (categoryId != null && !categoryId.isBlank()) {
            query.addCriteria(Criteria.where(TypedPropertyPath.path(Transaction::getCategoryId)).is(categoryId));
        }

        if (fromDate != null || toDate != null) {
            Criteria dateCriteria = Criteria.where(TypedPropertyPath.path(Transaction::getTransactionDate));

            if (fromDate != null) {
                dateCriteria = dateCriteria.gte(fromDate);
            }

            if (toDate != null) {
                dateCriteria = dateCriteria.lte(toDate);
            }

            query.addCriteria(dateCriteria);
        }

        query.with(Sort.by(
                Sort.Direction.DESC, TypedPropertyPath.path(Transaction::getTransactionDate)));

        return mongoTemplate.find(query, Transaction.class)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TransactionResponse getTransaction(String transactionId) {
        String ownerId = getCurrentOwnerId();

        Transaction transaction = transactionRepository
                .findByIdAndOwnerId(transactionId, ownerId)
                .orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Transaction not found."));

        return toResponse(transaction);
    }

    @Override
    public TransactionResponse updateTransaction(
            String transactionId,
            CreateTransactionRequest request) {

        String ownerId = getCurrentOwnerId();

        Transaction transaction = transactionRepository
                .findByIdAndOwnerId(transactionId, ownerId)
                .orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Transaction not found."));

        Category category = validateCategory(
                request.categoryId(), request.type());

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description().trim());
        transaction.setCategoryId(category.getId());
        transaction.setCategoryName(category.getName());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setNotes(request.notes());
        transaction.setUpdatedAt(Instant.now());

        return toResponse(transactionRepository.save(transaction));
    }

    @Override
    public void deleteTransaction(String transactionId) {
        String ownerId = getCurrentOwnerId();

        Transaction transaction = transactionRepository
                .findByIdAndOwnerId(transactionId, ownerId)
                .orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Transaction not found."));

        transactionRepository.delete(transaction);
    }

    private Category validateCategory(
            String categoryId,
            TransactionType transactionType) {

        Category category = categoryRepository
                .findByIdAndOwnerId(categoryId, getCurrentOwnerId())
                .or(() -> categoryRepository.findByIdAndIsDefaultTrue(categoryId))
                .orElseThrow(() -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Category not found."));

        if (category.getType() != transactionType) {
            throw new BusinessException(BusinessErrors.INVALID_REQUEST,
                    "Category type must match transaction type.");
        }

        return category;
    }

    private String getCurrentOwnerId() {
        return currentUser.userId().toString();
    }

    private TransactionResponse toResponse(Transaction transaction) {
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
}