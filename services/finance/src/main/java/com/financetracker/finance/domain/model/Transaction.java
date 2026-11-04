package com.financetracker.finance.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
@CompoundIndex(name = "idx_transaction_owner_date", def = "{'ownerId': 1, 'transactionDate': -1}")
public class Transaction {

    @Id
    private String id;

    private String ownerId;

    private TransactionType type;

    private BigDecimal amount;

    private String description;

    private String categoryId;

    // Snapshot: preserves the category name if the category is renamed.
    private String categoryName;

    private LocalDate transactionDate;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;
}