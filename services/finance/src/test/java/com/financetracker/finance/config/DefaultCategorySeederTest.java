package com.financetracker.finance.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class DefaultCategorySeederTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DefaultCategorySeeder defaultCategorySeeder;

    @Test
    void run_ShouldSeedAllDefaultCategoriesWhenNoneExist() {

        when(categoryRepository
                .findByNameIgnoreCaseAndTypeAndIsDefaultTrue(
                        anyString(),
                        any(TransactionType.class)))
                .thenReturn(Optional.empty());

        defaultCategorySeeder.run();

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository, times(15)).save(captor.capture());

        List<Category> savedCategories = captor.getAllValues();

        assertEquals(15, savedCategories.size());

        assertEquals(
                5,
                savedCategories.stream()
                        .filter(category -> category.getType() == TransactionType.INCOME)
                        .count());

        assertEquals(
                10,
                savedCategories.stream()
                        .filter(category -> category.getType() == TransactionType.EXPENSE)
                        .count());

        savedCategories.forEach(category -> {
            assertNull(category.getOwnerId());
            assertTrue(category.isDefault());
            assertNotNull(category.getName());
            assertNotNull(category.getIcon());
            assertNotNull(category.getCreatedAt());
            assertNotNull(category.getUpdatedAt());
        });
    }

    @Test
    void run_ShouldNotCreateDuplicatesWhenDefaultCategoriesAlreadyExist() {

        Category existingCategory = Category.builder()
                .name("Existing category")
                .type(TransactionType.EXPENSE)
                .isDefault(true)
                .build();

        when(categoryRepository
                .findByNameIgnoreCaseAndTypeAndIsDefaultTrue(
                        anyString(),
                        any(TransactionType.class)))
                .thenReturn(Optional.of(existingCategory));

        defaultCategorySeeder.run();

        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository, times(15))
                .findByNameIgnoreCaseAndTypeAndIsDefaultTrue(
                        anyString(),
                        any(TransactionType.class));
    }
}