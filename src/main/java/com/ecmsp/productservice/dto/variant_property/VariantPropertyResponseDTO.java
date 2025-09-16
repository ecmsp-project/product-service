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
    private String propertyName;
    private String propertyUnit;
    private PropertyDataType propertyDataType;

    private UUID propertyOptionId;
    private String displayText;

    private String customValueText;
    private BigDecimal customValueDecimal;
    private Boolean customValueBoolean;
    private LocalDate customValueDate;

}
