package com.ecmsp.productservice.dto.rest.variant;

import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record GetVariantRequestResponseDTO(
    VariantResponseDTO variant,
    Map<UUID, String> otherVariantsWithImageURL
) {
}

