package com.financetracker.finance.services;

import java.util.List;

import com.financetracker.finance.dto.request.CreateCategoryRequest;
import com.financetracker.finance.dto.response.CategoryResponse;
import com.financetracker.finance.domain.model.TransactionType;

public interface CategoryService {

    List<CategoryResponse> getCategories();

    List<CategoryResponse> getCategoriesByType(TransactionType type);

    CategoryResponse createCategory(CreateCategoryRequest request);

    void deleteCategory(String categoryId);
}