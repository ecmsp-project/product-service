package com.ecmsp.productservice.dto.rest;

import com.ecmsp.productservice.dto.variant_image.VariantImageResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record VariantDetailDTO(
    UUID variantId,
    String name,
    BigDecimal price,
    Integer stockQuantity,
    String description,
    List<VariantImageResponseDTO> variantImages,
    Map<String, Object> additionalProperties
) {

}
