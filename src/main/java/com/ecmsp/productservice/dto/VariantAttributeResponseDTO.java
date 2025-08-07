package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecmsp.productservice.domain.AttributeDataType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttributeResponseDTO {
    private UUID id;

    private UUID variantId;

    private UUID attributeId;
    private String attributeName;
    private String attributeUnit;
    private AttributeDataType attributeDataType;

    private UUID attributeValueId;

    private String valueText;
    private BigDecimal valueDecimal;
    private Boolean valueBoolean;
    private LocalDate valueDate;

    private String effectiveValue;
}
