package com.ecmsp.productservice.dto.variant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantResponseDTO {

    private UUID id;
    private UUID productId;

    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;
    private Map<String, Object> additionalProperties;
    private String description;
    private BigDecimal margin;

    private String productName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}