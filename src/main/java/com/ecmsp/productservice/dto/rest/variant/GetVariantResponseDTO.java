package com.ecmsp.productservice.dto.rest.variant;

import com.ecmsp.productservice.dto.rest.VariantDetailDTO;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GetVariantResponseDTO(
    VariantDetailDTO variant,

    List<String> selectablePropertyNames,
    List<Map<String, Object>> allVariants
) {
}

