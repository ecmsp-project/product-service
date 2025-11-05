package com.ecmsp.productservice.dto.rest.category;

import com.ecmsp.productservice.dto.category.CategoryResponseDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetCategoriesResponseDTO(
        List<CategoryResponseDTO> categories
) {
}
