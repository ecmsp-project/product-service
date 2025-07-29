package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecmsp.productservice.domain.AttributeDataType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeResponseDTO {
    private UUID id;
    private String name;
    private String unit;
    private AttributeDataType dataType;
    private boolean filterable;
    private UUID categoryId;
    private String categoryName;
    private int attributeValueCount;
    private int variantAttributeCount;
}