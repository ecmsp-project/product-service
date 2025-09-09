package com.ecmsp.productservice.dto;

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
public class VariantAttributeRequestDTO {
    private UUID id;
    private UUID variantId;
    private UUID attributeId;
    private UUID attributeValueId;
    private String valueText;
    private BigDecimal valueDecimal;
    private Boolean valueBoolean;
    private LocalDate valueDate;
}
