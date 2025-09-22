package com.ecmsp.productservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequestDTO {
    @Size(max = 255, message = "Category name cannot exceed 255 characters")
    private String name;

    private UUID parentCategoryId;
}