package com.ecmsp.productservice.dto.property_option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyOptionRequestDTO {

    @NotNull(message = "Attribute ID is required")
    private UUID propertyId;

    private String valueText;

    private BigDecimal valueDecimal;

    private LocalDate valueDate;

    private String displayText;
}