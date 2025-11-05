package com.ecmsp.productservice.dto.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record VariantDetailDTO(
    UUID variant_id,
    BigDecimal price,
    Integer stockQuantity,
    String imageUrl,
    String description,
    Map<String, Object> additionalProperties
) {

}
