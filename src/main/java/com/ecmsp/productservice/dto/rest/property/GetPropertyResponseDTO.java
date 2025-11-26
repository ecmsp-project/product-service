package com.ecmsp.productservice.dto.rest.property;

import com.ecmsp.productservice.domain.PropertyRole;
import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionResponseDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GetPropertyResponseDTO(
        UUID id,
        UUID categoryId,
        String name,
        String dataType,
        Boolean hasDefaultOptions,
        PropertyRole role,
        List<DefaultPropertyOptionResponseDTO> defaultPropertyOptions
) {
}
