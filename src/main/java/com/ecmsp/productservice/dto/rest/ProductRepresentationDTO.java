package com.ecmsp.productservice.dto.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductRepresentationDTO(
        UUID productId,
        String name,
        VariantDetailDTO variantDetail
) {

}
