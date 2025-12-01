package com.ecmsp.productservice.dto.variant_image;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record VariantImagesCreateResponseDTO(
        List<UUID> variantImageIds
) {
}
