package com.ecmsp.productservice.dto.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ecmsp.productservice.domain.PropertyDataType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyRequestDTO {
    @NotBlank(message = "Attribute name cannot be blank")
    @Size(max = 255, message = "Attribute name cannot exceed 255 characters")
    private String name;

    @Size(max = 50, message = "Unit cannot exceed 50 characters")
    private String unit;

    @NotNull(message = "Data type is required")
    private PropertyDataType dataType;

    @NotNull(message = "HasDefaultOptions field cannot be empty")
    private Boolean hasDefaultOptions;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
