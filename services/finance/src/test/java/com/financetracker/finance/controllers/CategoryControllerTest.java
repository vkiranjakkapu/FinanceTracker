package com.financetracker.finance.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateCategoryRequest;
import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.CategoryResponse;
import com.financetracker.finance.services.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse categoryResponse() {
        return new CategoryResponse(
                "category-1",
                "Food",
                TransactionType.EXPENSE,
                "#FF5733",
                "utensils",
                "Food expenses",
                false);
    }

    @Test
    void getCategories_ShouldReturnAllCategories() {

        List<CategoryResponse> categories = List.of(categoryResponse());

        when(categoryService.getCategories()).thenReturn(categories);

        ResponseEntity<ApiResponseDto<List<CategoryResponse>>> response =
                categoryController.getCategories(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody().data());

        verify(categoryService).getCategories();
    }

    @Test
    void getCategories_ShouldFilterByType() {

        List<CategoryResponse> categories = List.of(categoryResponse());

        when(categoryService.getCategoriesByType(TransactionType.EXPENSE))
                .thenReturn(categories);

        ResponseEntity<ApiResponseDto<List<CategoryResponse>>> response =
                categoryController.getCategories(TransactionType.EXPENSE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody().data());

        verify(categoryService).getCategoriesByType(TransactionType.EXPENSE);
    }

    @Test
    void createCategory_ShouldReturnCreatedResponse() {

        CreateCategoryRequest request = new CreateCategoryRequest(
                "Food",
                TransactionType.EXPENSE,
                "#FF5733",
                "Food expenses",
                "utensils");

        CategoryResponse expected = categoryResponse();

        when(categoryService.createCategory(request)).thenReturn(expected);

        ResponseEntity<ApiResponseDto<CategoryResponse>> response =
                categoryController.createCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(categoryService).createCategory(request);
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() {

        ResponseEntity<ApiResponseDto<Void>> response =
                categoryController.deleteCategory("category-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoryService).deleteCategory("category-1");
    }
}