package com.ecmsp.productservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    private UUID id;
    private String name;

    private UUID parentCategoryId;
    private String parentCategoryName;

    private int subCategoryCount;
    private int productCount;
    private int propertyCount;
}
