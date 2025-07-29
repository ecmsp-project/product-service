package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.CategoryRequestDTO;
import com.ecmsp.productservice.dto.CategoryResponseDTO;
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
        CategoryResponseDTO.CategoryResponseDTOBuilder dtoBuilder = CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName());

        if (category.getParentCategory() != null) {
            dtoBuilder.parentCategoryId(category.getParentCategory().getId())
                    .parentCategoryName(category.getParentCategory().getName());
        }

        dtoBuilder.subCategoryCount(category.getSubCategories() != null ? category.getSubCategories().size() : 0);
        dtoBuilder.productCount(category.getProducts() != null ? category.getProducts().size() : 0);
        dtoBuilder.attributeCount(category.getAttributes() != null ? category.getAttributes().size() : 0);

        return dtoBuilder.build();
    }

    private Category convertToEntity(CategoryRequestDTO categoryRequestDTO) {
        Category category = Category.builder()
                .name(categoryRequestDTO.getName())
                .build();

        if (categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", categoryRequestDTO.getParentCategoryId()));
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
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return convertToDto(category);
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = convertToEntity(categoryRequestDTO);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO categoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        existingCategory.setName(categoryRequestDTO.getName());

        if ((categoryRequestDTO.getParentCategoryId() == null && existingCategory.getParentCategory() != null) ||
                (categoryRequestDTO.getParentCategoryId() != null && (existingCategory.getParentCategory() == null ||
                        !existingCategory.getParentCategory().getId().equals(categoryRequestDTO.getParentCategoryId())))) {

            if (categoryRequestDTO.getParentCategoryId() != null) {
                if (id.equals(categoryRequestDTO.getParentCategoryId())) {
                    throw new IllegalArgumentException("A category cannot be its own parent.");
                }
                Category newParent = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent Category", categoryRequestDTO.getParentCategoryId()));
                existingCategory.setParentCategory(newParent);
            } else {
                existingCategory.setParentCategory(null);
            }
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
    }
}