package com.ecmsp.productservice.dto.variant_image;

import lombok.Builder;

import java.util.UUID;

@Builder
public record VariantImageCreateRequestDTO(
        UUID id,
        UUID variantId,
        String url
) {
}