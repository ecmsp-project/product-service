package com.ecmsp.productservice.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequestDTO {
    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 255, message = "Category name cannot exceed 255 characters")
    private String name;

    private UUID childCategoryId;
    private UUID parentCategoryId;
}
