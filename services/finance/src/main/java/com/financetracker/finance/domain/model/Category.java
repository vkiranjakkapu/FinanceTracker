package com.financetracker.finance.domain.model;

import java.time.Instant;

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
@Document(collection = "categories")
@CompoundIndex(name = "uk_category_owner_name_type", def = "{'ownerId': 1, 'name': 1, 'type': 1}", unique = true)
public class Category {

    @Id
    private String id;

    private String ownerId;

    private String name;

    private TransactionType type;

    @Builder.Default
    private boolean isDefault = false;

    private String description;

    private String color;

    private String icon;

    private Instant createdAt;

    private Instant updatedAt;
}