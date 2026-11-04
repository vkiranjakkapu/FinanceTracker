package com.financetracker.finance.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateCategoryRequest;
import com.financetracker.finance.dto.response.CategoryResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.services.CategoryService;
import com.financetracker.finance.services.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUser;

    @Override
    public List<CategoryResponse> getCategories() {
        String ownerId = getCurrentOwnerId();

        return categoryRepository
                .findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getCategoriesByType(TransactionType type) {
        String ownerId = getCurrentOwnerId();

        return categoryRepository
                .findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(ownerId)
                .stream()
                .filter(category -> category.getType() == type)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String ownerId = getCurrentOwnerId();

        boolean duplicateExists = categoryRepository
                .findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(ownerId)
                .stream()
                .anyMatch(category -> category.getType() == request.type()
                        && category.getName().equalsIgnoreCase(request.name()));

        if (duplicateExists) {
            throw new BusinessException(BusinessErrors.DUPLICATE_RESOURCE,
                    "A category with this name and type already exists.");
        }

        Instant now = Instant.now();

        Category category = Category.builder()
                .ownerId(ownerId)
                .name(request.name().trim())
                .type(request.type())
                .color(request.color())
                .icon(request.icon())
                .description(request.description())
                .isDefault(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(String categoryId) {
        String ownerId = getCurrentOwnerId();

        Category category = categoryRepository
                .findByIdAndOwnerId(categoryId, ownerId)
                .orElseThrow(
                        () -> new BusinessException(BusinessErrors.RESOURCE_NOT_FOUND, "Custom category not found."));

        if (category.isDefault()) {
            throw new BusinessException(BusinessErrors.INVALID_REQUEST,
                    "Default categories cannot be deleted.");
        }

        categoryRepository.delete(category);
    }

    private String getCurrentOwnerId() {
        return currentUser.userId().toString();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor(),
                category.getIcon(),
                category.getDescription(),
                category.isDefault());
    }
}