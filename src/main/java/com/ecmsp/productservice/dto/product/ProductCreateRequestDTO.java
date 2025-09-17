package com.ecmsp.productservice.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDTO {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Approximate price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Approximate price must be positive")
    private BigDecimal approximatePrice;

    @NotNull(message = "Delivery price is required")
    @DecimalMin(value = "0.0", message = "Delivery price cannot be negative")
    private BigDecimal deliveryPrice;

    private String description;

    private Map<String, Object> info;


}
