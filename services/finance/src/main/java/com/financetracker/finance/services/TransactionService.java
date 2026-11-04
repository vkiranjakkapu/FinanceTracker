package com.financetracker.finance.services;

import java.time.LocalDate;
import java.util.List;

import com.financetracker.finance.dto.request.CreateTransactionRequest;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.domain.model.TransactionType;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest request);

    List<TransactionResponse> getTransactions(
            TransactionType type,
            String categoryId,
            LocalDate fromDate,
            LocalDate toDate);

    TransactionResponse getTransaction(String transactionId);

    TransactionResponse updateTransaction(
            String transactionId,
            CreateTransactionRequest request);

    void deleteTransaction(String transactionId);
}