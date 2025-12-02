package com.ecmsp.productservice.dto.variant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantCreateRequestDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    @NotNull(message = "Additional attributes are required")
    private Map<String, Object> additionalProperties;

    private String description;

    @DecimalMin(value = "5", message = "Margin cannot be less than 5%")
    @DecimalMax(value = "80", message = "Margin cannot exceed 80%")
    private BigDecimal margin;
}
