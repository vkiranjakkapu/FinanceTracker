
package com.financetracker.finance.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financetracker.finance.domain.model.Category;
import com.financetracker.finance.domain.model.TransactionType;
import com.financetracker.finance.dto.request.CreateCategoryRequest;
import com.financetracker.finance.dto.response.CategoryResponse;
import com.financetracker.finance.enums.BusinessErrors;
import com.financetracker.finance.exceptions.BusinessException;
import com.financetracker.finance.repository.CategoryRepository;
import com.financetracker.finance.services.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

	private static final UUID USER_ID = UUID.fromString("c0186249-9fc1-4927-97b3-a08a21febfe3");

	private static final String CATEGORY_ID = "category-1";

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CurrentUserService currentUser;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	private Category expenseCategory;
	private Category incomeCategory;

	@BeforeEach
	void setUp() {

		when(currentUser.userId()).thenReturn(USER_ID);

		expenseCategory = Category.builder()
				.id(CATEGORY_ID)
				.ownerId(USER_ID.toString())
				.name("Food")
				.type(TransactionType.EXPENSE)
				.color("#FF5733")
				.icon("utensils")
				.description("Food expenses")
				.isDefault(false)
				.build();

		incomeCategory = Category.builder()
				.id("category-2")
				.ownerId(USER_ID.toString())
				.name("Salary")
				.type(TransactionType.INCOME)
				.isDefault(true)
				.build();
	}

	@Test
	void getCategories_ShouldReturnUserAndDefaultCategories() {

		when(categoryRepository
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString()))
				.thenReturn(List.of(expenseCategory, incomeCategory));

		List<CategoryResponse> responses = categoryService.getCategories();

		assertEquals(2, responses.size());

		assertEquals(CATEGORY_ID, responses.get(0).id());
		assertEquals("Food", responses.get(0).name());
		assertEquals(TransactionType.EXPENSE, responses.get(0).type());
		assertEquals("#FF5733", responses.get(0).color());
		assertEquals("utensils", responses.get(0).icon());
		assertEquals("Food expenses", responses.get(0).description());
		assertFalse(responses.get(0).isDefault());

		assertEquals("Salary", responses.get(1).name());
		assertTrue(responses.get(1).isDefault());

		verify(categoryRepository)
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString());
	}

	@Test
	void getCategoriesByType_ShouldReturnOnlyMatchingCategories() {

		when(categoryRepository
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString()))
				.thenReturn(List.of(expenseCategory, incomeCategory));

		List<CategoryResponse> responses = categoryService.getCategoriesByType(
				TransactionType.EXPENSE);

		assertEquals(1, responses.size());
		assertEquals("Food", responses.get(0).name());
		assertEquals(TransactionType.EXPENSE, responses.get(0).type());
	}

	@Test
	void createCategory_ShouldSaveCustomCategory() {

		CreateCategoryRequest request = new CreateCategoryRequest(
				"  Travel  ",
				TransactionType.EXPENSE,
				"#123456",
				"Travel expenses",
				"plane");

		when(categoryRepository
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString()))
				.thenReturn(List.of());

		when(categoryRepository.save(any(Category.class)))
				.thenAnswer(invocation -> {
					Category category = invocation.getArgument(0);
					category.setId(CATEGORY_ID);
					return category;
				});

		CategoryResponse response = categoryService.createCategory(request);

		assertNotNull(response);
		assertEquals(CATEGORY_ID, response.id());
		assertEquals("Travel", response.name());
		assertEquals(TransactionType.EXPENSE, response.type());
		assertEquals("#123456", response.color());
		assertEquals("plane", response.icon());
		assertEquals("Travel expenses", response.description());
		assertFalse(response.isDefault());

		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void createCategory_ShouldRejectDuplicateCategory() {

		CreateCategoryRequest request = new CreateCategoryRequest(
				"food",
				TransactionType.EXPENSE,
				"#123456",
				"Duplicate category",
				"utensils");

		when(categoryRepository
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString()))
				.thenReturn(List.of(expenseCategory));

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> categoryService.createCategory(request));

		assertEquals(
				BusinessErrors.DUPLICATE_RESOURCE.name(),
				exception.getDefinition().getErrorName());

		verify(categoryRepository, never())
				.save(any(Category.class));
	}

	@Test
	void createCategory_ShouldAllowSameNameWithDifferentType() {

		CreateCategoryRequest request = new CreateCategoryRequest(
				"Food",
				TransactionType.INCOME,
				"#123456",
				"Food income",
				"food");

		when(categoryRepository
				.findAllByOwnerIdOrIsDefaultTrueOrderByTypeAscNameAsc(
						USER_ID.toString()))
				.thenReturn(List.of(expenseCategory));

		when(categoryRepository.save(any(Category.class)))
				.thenAnswer(invocation -> {
					Category category = invocation.getArgument(0);
					category.setId("category-3");
					return category;
				});

		CategoryResponse response = categoryService.createCategory(request);

		assertEquals(TransactionType.INCOME, response.type());
		assertEquals("Food", response.name());

		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void deleteCategory_ShouldDeleteCustomCategory() {

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.of(expenseCategory));

		categoryService.deleteCategory(CATEGORY_ID);

		verify(categoryRepository).delete(expenseCategory);
	}

	@Test
	void deleteCategory_ShouldRejectDefaultCategory() {

		Category defaultCategory = Category.builder()
				.id(CATEGORY_ID)
				.ownerId(USER_ID.toString())
				.name("Food")
				.type(TransactionType.EXPENSE)
				.isDefault(true)
				.build();

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.of(defaultCategory));

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> categoryService.deleteCategory(CATEGORY_ID));

		assertEquals(
				BusinessErrors.INVALID_REQUEST.name(),
				exception.getDefinition().getErrorName());

		verify(categoryRepository, never())
				.delete(any(Category.class));
	}

	@Test
	void deleteCategory_ShouldThrowWhenCategoryNotOwnedByUser() {

		when(categoryRepository.findByIdAndOwnerId(
				CATEGORY_ID, USER_ID.toString()))
				.thenReturn(Optional.empty());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> categoryService.deleteCategory(CATEGORY_ID));

		assertEquals(
				BusinessErrors.RESOURCE_NOT_FOUND.name(),
				exception.getDefinition().getErrorName());

		verify(categoryRepository, never())
				.delete(any(Category.class));
	}
}
