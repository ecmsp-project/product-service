package com.ecmsp.productservice.dto.variant_property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecmsp.productservice.domain.PropertyDataType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPropertyResponseDTO {
    private UUID id;

    private UUID variantId;
    private UUID propertyId;

    private PropertyDataType propertyDataType;

    private String valueText;
    private BigDecimal valueDecimal;
    private Boolean valueBoolean;
    private LocalDate valueDate;

    private String displayText;

    private Boolean isDefaultPropertyOption;
    private Boolean isRequired;
}
