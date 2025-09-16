package com.ecmsp.productservice.dto.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

import com.ecmsp.productservice.domain.PropertyDataType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyUpdateRequestDTO {
    @Size(max = 255, message = "Property name cannot exceed 255 characters")
    private String name;

    @Size(max = 50, message = "Unit cannot exceed 50 characters")
    private String unit;

    private PropertyDataType dataType;

    private Boolean required;

    private Boolean filterable;

    private UUID categoryId;
}