package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.category.CategoryResponseDTO;
import com.ecmsp.productservice.dto.rest.category.GetCategoriesRequestDTO;
import com.ecmsp.productservice.dto.rest.category.GetCategoriesResponseDTO;
import com.ecmsp.productservice.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<GetCategoriesResponseDTO> getCategories(
            GetCategoriesRequestDTO request
    ) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();

        GetCategoriesResponseDTO response = GetCategoriesResponseDTO.builder()
                .categories(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<GetCategoriesResponseDTO> getSubcategories(
            @PathVariable UUID categoryId
    ) {
        List<CategoryResponseDTO> categories = categoryService.getSubcategories(categoryId);
        GetCategoriesResponseDTO response = GetCategoriesResponseDTO.builder()
                .categories(categories)
                .build();

        return ResponseEntity.ok(response);
    }
}
