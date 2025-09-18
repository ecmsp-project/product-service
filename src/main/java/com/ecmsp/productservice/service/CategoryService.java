package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.category.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private CategoryResponseDTO convertToDto(Category category) {
        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
            response.setParentCategoryName(category.getParentCategory().getName());
        }

        response.setSubCategoryCount(category.getSubCategories().size());
        response.setProductCount(category.getProducts().size());
        response.setPropertyCount(category.getProperties().size());

        return response;
    }

    private Category convertToEntity(CategoryCreateRequestDTO request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }
        return category;
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Transactional
    public CategoryCreateResponseDTO createCategoryLeaf(CategoryCreateRequestDTO request) {
        Category category = convertToEntity(request);
        Category savedCategory = categoryRepository.save(category);

        return CategoryCreateResponseDTO
                .builder()
                .id(savedCategory.getId())
                .build();
    }

    @Transactional
    public CategoryCreateResponseDTO createCategorySplit(CategoryCreateRequestDTO request) {
        Category category = convertToEntity(request);
        Category savedCategory = categoryRepository.save(category);

        for (Category childCategory : savedCategory.getParentCategory().getSubCategories()) {
            childCategory.setParentCategory(savedCategory);
            categoryRepository.save(childCategory);
        }

        return CategoryCreateResponseDTO.builder()
                .id(savedCategory.getId())
                .build();
    }

    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryUpdateRequestDTO request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        if (request.getName() != null) {
            existingCategory.setName(request.getName());
        }

        if (request.getParentCategoryId() != null) {

            if (request.getParentCategoryId().equals(existingCategory.getId())) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }

            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found", request.getParentCategoryId()));
            existingCategory.setParentCategory(parentCategory);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        for (Category childCategory : category.getSubCategories()) {
            childCategory.setParentCategory(category.getParentCategory());
            categoryRepository.save(childCategory);
        }

        categoryRepository.deleteById(id);
    }
}
