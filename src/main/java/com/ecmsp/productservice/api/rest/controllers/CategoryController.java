package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.category.CategoryResponseDTO;
import com.ecmsp.productservice.dto.rest.category.GetCategoriesRequestDTO;
import com.ecmsp.productservice.dto.rest.category.GetCategoriesResponseDTO;
import com.ecmsp.productservice.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/subcategories")
    public ResponseEntity<GetCategoriesResponseDTO> getSubcategories(
            @RequestParam(required = false) UUID categoryId
    ) {
        List<CategoryResponseDTO> categories;

        if (categoryId != null) {
            categories = categoryService.getSubcategories(categoryId);
        } else {
            categories = categoryService.getCategoriesByParentCategoryID(null);
        }

        GetCategoriesResponseDTO response = GetCategoriesResponseDTO.builder()
                .categories(categories)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<GetCategoriesResponseDTO> getCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();

        GetCategoriesResponseDTO response = GetCategoriesResponseDTO.builder()
                .categories(categories)
                .build();
        return ResponseEntity.ok(response);
    }




}
