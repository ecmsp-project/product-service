package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.category.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.CategoryRepository;
import com.ecmsp.productservice.util.TestEntitiesBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private Category parentCategory;
    private UUID categoryId;
    private UUID parentCategoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        parentCategoryId = UUID.randomUUID();

        parentCategory = TestEntitiesBuilder.categoryBuilder()
                .id(parentCategoryId)
                .name("Parent Category")
                .build();

        testCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .name("Test Category")
                .parentCategory(parentCategory)
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategoryResponseDTO() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));

        // When
        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(categoryId);
        assertThat(result.get(0).getName()).isEqualTo(testCategory.getName());
        assertThat(result.get(0).getParentCategoryId()).isEqualTo(parentCategoryId);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategoryResponseDTO() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When
        CategoryResponseDTO result = categoryService.getCategoryById(categoryId);

        // Then
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo(testCategory.getName());
        assertThat(result.getParentCategoryId()).isEqualTo(parentCategoryId);
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void createCategoryLeaf_WhenParentExists_ShouldReturnCategoryCreateResponseDTO() {
        // Given
        CategoryCreateRequestDTO request = CategoryCreateRequestDTO.builder()
                .name("New Category")
                .parentCategoryId(parentCategoryId)
                .build();

        Category savedCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .name("New Category")
                .parentCategory(parentCategory)
                .build();

        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryCreateResponseDTO result = categoryService.createCategoryLeaf(request);

        // Then
        assertThat(result.getId()).isEqualTo(categoryId);
        verify(categoryRepository).findById(parentCategoryId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategoryLeaf_WithoutParent_ShouldCreateRootCategory() {
        // Given
        CategoryCreateRequestDTO request = CategoryCreateRequestDTO.builder()
                .name("Root Category")
                .build();

        Category savedCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .name("Root Category")
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryCreateResponseDTO result = categoryService.createCategoryLeaf(request);

        // Then
        assertThat(result.getId()).isEqualTo(categoryId);
        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategoryLeaf_WhenParentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        CategoryCreateRequestDTO request = CategoryCreateRequestDTO.builder()
                .name("New Category")
                .parentCategoryId(parentCategoryId)
                .build();

        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategoryLeaf(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Parent category not found");
        verify(categoryRepository).findById(parentCategoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void createCategorySplit_ShouldMoveSubCategoriesToNewCategory() {
        // Given
        Category childCategory1 = TestEntitiesBuilder.categoryBuilder()
                .id(UUID.randomUUID())
                .name("Child 1")
                .parentCategory(parentCategory)
                .build();

        Category childCategory2 = TestEntitiesBuilder.categoryBuilder()
                .id(UUID.randomUUID())
                .name("Child 2")
                .parentCategory(parentCategory)
                .build();

        parentCategory.getSubCategories().addAll(Set.of(childCategory1, childCategory2));

        CategoryCreateRequestDTO request = CategoryCreateRequestDTO.builder()
                .name("Split Category")
                .parentCategoryId(parentCategoryId)
                .build();

        Category savedCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .name("Split Category")
                .parentCategory(parentCategory)
                .build();

        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryCreateResponseDTO result = categoryService.createCategoryAllSplit(request);

        // Then
        assertThat(result.getId()).isEqualTo(categoryId);
        verify(categoryRepository).findById(parentCategoryId);
        verify(categoryRepository, times(3)).save(any(Category.class)); // New category + 2 children
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldUpdateFields() {
        // Given
        UUID newParentId = UUID.randomUUID();
        Category newParent = TestEntitiesBuilder.categoryBuilder()
                .id(newParentId)
                .build();

        CategoryUpdateRequestDTO request = CategoryUpdateRequestDTO.builder()
                .name("Updated Category")
                .parentCategoryId(newParentId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.of(newParent));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryResponseDTO result = categoryService.updateCategory(categoryId, request);

        // Then
        assertThat(result.getId()).isEqualTo(categoryId);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(newParentId);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        CategoryUpdateRequestDTO request = CategoryUpdateRequestDTO.builder()
                .name("Updated Category")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenTryingToSetSelfAsParent_ShouldThrowIllegalArgumentException() {
        // Given
        CategoryUpdateRequestDTO request = CategoryUpdateRequestDTO.builder()
                .parentCategoryId(categoryId) // Same as category ID
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("category cannot be its own parent");
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNewParentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        UUID newParentId = UUID.randomUUID();
        CategoryUpdateRequestDTO request = CategoryUpdateRequestDTO.builder()
                .parentCategoryId(newParentId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Parent category not found");
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(newParentId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDeleteCategoryAndReassignChildren() {
        // Given
        Category childCategory1 = TestEntitiesBuilder.categoryBuilder()
                .id(UUID.randomUUID())
                .name("Child 1")
                .parentCategory(testCategory)
                .build();

        Category childCategory2 = TestEntitiesBuilder.categoryBuilder()
                .id(UUID.randomUUID())
                .name("Child 2")
                .parentCategory(testCategory)
                .build();

        testCategory.getSubCategories().addAll(Set.of(childCategory1, childCategory2));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, times(2)).save(any(Category.class)); // 2 children
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }
}