package com.ecmsp.productservice.dto.variant;

import jakarta.validation.constraints.*;
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
public class VariantCreateRequestDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "SKU cannot be blank")
    @Size(max = 12, message = "SKU cannot exceed 12 characters")
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;

    @NotNull(message = "Additional attributes are required")
    private Map<String, Object> additionalProperties;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
