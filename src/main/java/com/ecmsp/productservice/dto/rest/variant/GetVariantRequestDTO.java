package com.ecmsp.productservice.dto.rest.variant;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetVariantRequestDTO(
        UUID variantId
) {
}
