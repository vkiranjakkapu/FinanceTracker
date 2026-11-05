package com.financetracker.finance.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateTransactionRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.services.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionResponse transactionResponse() {
        return new TransactionResponse(
                "transaction-1",
                TransactionType.EXPENSE,
                new BigDecimal("500.00"),
                "Groceries",
                "category-1",
                "Food",
                LocalDate.of(2026, 11, 5),
                "Weekly groceries",
                null,
                null);
    }

    @Test
    void getTransactions_ShouldReturnTransactions() {

        List<TransactionResponse> transactions =
                List.of(transactionResponse());

        when(transactionService.getTransactions(
                TransactionType.EXPENSE,
                "category-1",
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30)))
                .thenReturn(transactions);

        ResponseEntity<ApiResponseDto<List<TransactionResponse>>> response =
                transactionController.getTransactions(
                        TransactionType.EXPENSE,
                        "category-1",
                        LocalDate.of(2026, 11, 1),
                        LocalDate.of(2026, 11, 30));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody().data());

        verify(transactionService).getTransactions(
                TransactionType.EXPENSE,
                "category-1",
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30));
    }

    @Test
    void getTransactions_ShouldSupportNoFilters() {

        when(transactionService.getTransactions(
                null, null, null, null))
                .thenReturn(List.of());

        ResponseEntity<ApiResponseDto<List<TransactionResponse>>> response =
                transactionController.getTransactions(
                        null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody().data());

        verify(transactionService).getTransactions(
                null, null, null, null);
    }

    @Test
    void getTransaction_ShouldReturnTransaction() {

        TransactionResponse expected = transactionResponse();

        when(transactionService.getTransaction("transaction-1"))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<TransactionResponse>> response =
                transactionController.getTransaction("transaction-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(transactionService).getTransaction("transaction-1");
    }

    @Test
    void createTransaction_ShouldReturnCreatedResponse() {

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.EXPENSE,
                new BigDecimal("500.00"),
                "Groceries",
                "category-1",
                LocalDate.of(2026, 11, 5),
                "Weekly groceries");

        TransactionResponse expected = transactionResponse();

        when(transactionService.createTransaction(request))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<TransactionResponse>> response =
                transactionController.createTransaction(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(transactionService).createTransaction(request);
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedResponse() {

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.EXPENSE,
                new BigDecimal("750.00"),
                "Updated groceries",
                "category-1",
                LocalDate.of(2026, 11, 5),
                "Updated notes");

        TransactionResponse expected = transactionResponse();

        when(transactionService.updateTransaction(
                "transaction-1", request))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<TransactionResponse>> response =
                transactionController.updateTransaction(
                        "transaction-1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(transactionService).updateTransaction(
                "transaction-1", request);
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() {

        ResponseEntity<ApiResponseDto<Void>> response =
                transactionController.deleteTransaction("transaction-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(transactionService).deleteTransaction("transaction-1");
    }
}