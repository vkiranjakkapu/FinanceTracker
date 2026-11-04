
package com.financetracker.finance.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateCategoryRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.CategoryResponse;
import com.financetracker.finance.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CategoryResponse>>> getCategories(
            @RequestParam(required = false) TransactionType type) {

        List<CategoryResponse> categories = type == null
                ? categoryService.getCategories()
                : categoryService.getCategoriesByType(type);

        return ResponseEntity.ok(ApiResponseDto.<List<CategoryResponse>>builder().data(categories).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.<CategoryResponse>builder().data(response).build());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCategory(
            @PathVariable String categoryId) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
