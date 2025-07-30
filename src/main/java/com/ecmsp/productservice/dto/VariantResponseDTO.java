package com.ecmsp.productservice.dto;

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
    private String sku;
    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;
    private Map<String, Object> additionalAttributes;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID productId;
    private String productName;
    private int variantAttributeCount;
}