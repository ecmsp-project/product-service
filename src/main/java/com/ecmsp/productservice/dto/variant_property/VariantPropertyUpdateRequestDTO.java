package com.ecmsp.productservice.dto.variant_property;

import com.ecmsp.productservice.service.VariantPropertyService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPropertyUpdateRequestDTO implements VariantPropertyService.PropertyOptionRequest {
    private UUID variantId;

    private UUID propertyId;

    @Size(max = 255, message = "Text value cannot exceed 255 characters")
    private String valueText;

    private BigDecimal valueDecimal;

    private Boolean valueBoolean;

    private LocalDate valueDate;

    private String displayText;
}