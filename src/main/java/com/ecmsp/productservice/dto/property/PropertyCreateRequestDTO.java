package com.ecmsp.productservice.dto.property;

import com.ecmsp.productservice.domain.PropertyDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PropertyCreateRequestDTO {
    @NotBlank(message = "Property name cannot be blank")
    @Size(max = 255, message = "Property name cannot exceed 255 characters")
    private String name;

    @Size(max = 50, message = "Unit cannot exceed 50 characters")
    private String unit;

    @NotNull(message = "Data type is required")
    private PropertyDataType dataType;

    @NotNull(message = "Filterable status is required")
    private Boolean filterable;

    @NotNull(message = "Required field cannot be empty")
    private Boolean required;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
