package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttributeRequestDTO {
    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    @NotNull(message = "Attribute ID is required")
    private UUID attributeId;

    private UUID attributeValueId;

    @Size(max = 255, message = "Text value cannot exceed 255 characters")
    private String valueText;

    private BigDecimal valueDecimal;

    private Boolean valueBoolean;

    private LocalDate valueDate;
}
