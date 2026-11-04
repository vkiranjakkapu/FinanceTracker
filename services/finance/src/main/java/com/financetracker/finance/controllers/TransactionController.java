
package com.financetracker.finance.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateTransactionRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.TransactionResponse;
import com.financetracker.finance.services.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@GetMapping
	public ResponseEntity<ApiResponseDto<List<TransactionResponse>>> getTransactions(
			@RequestParam(required = false) TransactionType type,
			@RequestParam(required = false) String categoryId,
			@RequestParam(required = false) LocalDate fromDate,
			@RequestParam(required = false) LocalDate toDate) {

		List<TransactionResponse> transactions = transactionService.getTransactions(
				type, categoryId, fromDate, toDate);

		return ResponseEntity.ok(ApiResponseDto.<List<TransactionResponse>>builder().data(transactions).build());
	}

	@GetMapping("/{transactionId}")
	public ResponseEntity<ApiResponseDto<TransactionResponse>> getTransaction(
			@PathVariable String transactionId) {

		TransactionResponse response = transactionService.getTransaction(transactionId);

		return ResponseEntity.ok(ApiResponseDto.<TransactionResponse>builder().data(response).build());
	}

	@PostMapping
	public ResponseEntity<ApiResponseDto<TransactionResponse>> createTransaction(
			@Valid @RequestBody CreateTransactionRequest request) {

		TransactionResponse response = transactionService.createTransaction(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseDto.<TransactionResponse>builder().data(response).build());
	}

	@PutMapping("/{transactionId}")
	public ResponseEntity<ApiResponseDto<TransactionResponse>> updateTransaction(
			@PathVariable String transactionId,
			@Valid @RequestBody CreateTransactionRequest request) {

		TransactionResponse response = transactionService.updateTransaction(transactionId, request);

		return ResponseEntity.ok(ApiResponseDto.<TransactionResponse>builder().data(response).build());
	}

	@DeleteMapping("/{transactionId}")
	public ResponseEntity<ApiResponseDto<Void>> deleteTransaction(
			@PathVariable String transactionId) {

		transactionService.deleteTransaction(transactionId);

		return ResponseEntity.noContent().build();
	}
}
