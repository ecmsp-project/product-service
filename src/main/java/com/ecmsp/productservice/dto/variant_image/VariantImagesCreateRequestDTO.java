package com.ecmsp.productservice.dto.variant_image;

import lombok.Builder;

import java.util.List;

@Builder
public record VariantImagesCreateRequestDTO(
        List<String> variantImages
) {
}
