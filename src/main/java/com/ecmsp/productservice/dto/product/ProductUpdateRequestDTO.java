package com.ecmsp.productservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequestDTO {
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Approximate price must be positive")
    private BigDecimal approximatePrice;

    @DecimalMin(value = "0.0", message = "Delivery price cannot be negative")
    private BigDecimal deliveryPrice;

    private String description;

    private Map<String, Object> info;

    private UUID categoryId;
}