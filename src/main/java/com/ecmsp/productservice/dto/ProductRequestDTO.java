package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Approximate price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Approximate price must be positive")
    private BigDecimal approximatePrice;

    @NotNull(message = "Delivery price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Delivery price cannot be negative")
    private BigDecimal deliveryPrice;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Info JSON is required")
    private Map<String, Object> info;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}