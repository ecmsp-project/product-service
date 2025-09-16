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
public class CategoryCreateResponseDTO {
    private UUID id;

    private UUID parentCategoryId;
    private String parentCategoryName;
}
