package com.ecmsp.productservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record VariantImageResponseDTO(
        UUID id,
        UUID variantId,
        String url,
        Boolean isMain,
        Integer position
) {
}
