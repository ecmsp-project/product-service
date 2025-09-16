package com.ecmsp.productservice.dto.property_option;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyOptionCreateRequestDTO {
    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    private String valueText;
    private BigDecimal valueDecimal;
    private Boolean valueBoolean;
    private LocalDate valueDate;

    private String displayText;
}
