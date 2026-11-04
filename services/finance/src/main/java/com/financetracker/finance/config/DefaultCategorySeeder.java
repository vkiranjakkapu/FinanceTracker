package com.financetracker.finance.config;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultCategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        seedCategories();
    }

    private void seedCategories() {
        List<Category> defaults = List.of(
                // Income
                defaultCategory("Salary", TransactionType.INCOME, "💰"),
                defaultCategory("Business", TransactionType.INCOME, "💼"),
                defaultCategory("Investments", TransactionType.INCOME, "📈"),
                defaultCategory("Freelance", TransactionType.INCOME, "💻"),
                defaultCategory("Other Income", TransactionType.INCOME, "➕"),

                // Expenses
                defaultCategory("Food", TransactionType.EXPENSE, "🍔"),
                defaultCategory("Groceries", TransactionType.EXPENSE, "🛒"),
                defaultCategory("Rent", TransactionType.EXPENSE, "🏠"),
                defaultCategory("Travel", TransactionType.EXPENSE, "🚗"),
                defaultCategory("Utilities", TransactionType.EXPENSE, "💡"),
                defaultCategory("Healthcare", TransactionType.EXPENSE, "🏥"),
                defaultCategory("Shopping", TransactionType.EXPENSE, "🛍️"),
                defaultCategory("Entertainment", TransactionType.EXPENSE, "🎬"),
                defaultCategory("Education", TransactionType.EXPENSE, "📚"),
                defaultCategory("Other Expense", TransactionType.EXPENSE, "➖")
        );

        for (Category category : defaults) {
            boolean exists = categoryRepository
                    .findByNameIgnoreCaseAndTypeAndIsDefaultTrue(
                            category.getName(),
                            category.getType())
                    .isPresent();

            if (!exists) {
                categoryRepository.save(category);
            }
        }
    }

    private Category defaultCategory(
            String name,
            TransactionType type,
            String icon) {

        Instant now = Instant.now();

        return Category.builder()
                .ownerId(null)
                .name(name)
                .type(type)
                .icon(icon)
                .isDefault(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}