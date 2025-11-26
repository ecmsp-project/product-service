package com.ecmsp.productservice.dto.rest;

import com.ecmsp.productservice.domain.VariantImage;
import com.ecmsp.productservice.dto.VariantImageResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record VariantDetailDTO(
    UUID variant_id,
    BigDecimal price,
    Integer stockQuantity,
    String description,
    List<VariantImageResponseDTO> variantImages,
    Map<String, Object> additionalProperties
) {
    public List<VariantImageResponseDTO> convertToVariantImageResponseDTO(List<VariantImage> variantImages) {

    }
}
