package com.ecmsp.productservice.dto.variant_property;

import com.ecmsp.productservice.service.VariantPropertyService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPropertyCreateRequestDTO implements VariantPropertyService.PropertyOptionRequest {
    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    private UUID propertyOptionId;

    @Size(max = 255, message = "Text value cannot exceed 255 characters")
    private String customValueText;

    private BigDecimal customValueDecimal;

    private Boolean customValueBoolean;

    private LocalDate customValueDate;
}
