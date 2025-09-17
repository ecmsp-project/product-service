package com.ecmsp.productservice.dto.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecmsp.productservice.domain.PropertyDataType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponseDTO {
    private UUID id;
    private UUID categoryId;

    private String name;
    private String unit;
    private PropertyDataType dataType;

    private boolean required;
    private boolean hasDefaultOptions;

    private int propertyValueCount;
    private int variantPropertyCount;
}