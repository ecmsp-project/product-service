package com.ecmsp.productservice.dto.default_property_option;

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
public class DefaultPropertyOptionUpdateRequestDTO {

    private UUID propertyId;

    private String valueText;
    private BigDecimal valueDecimal;
    private LocalDate valueDate;
    private Boolean valueBoolean;

    private String displayText;
}