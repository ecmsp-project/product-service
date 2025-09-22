package com.ecmsp.productservice.dto.default_property_option;


import com.ecmsp.productservice.domain.DefaultPropertyOption;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
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
public class DefaultPropertyOptionCreateRequestDTO {
    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    private String valueText;
    private BigDecimal valueDecimal;
    private Boolean valueBoolean;
    private LocalDate valueDate;

    private String displayText;


}
