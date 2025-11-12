package com.ecmsp.productservice.dto.rest.property;

import lombok.Builder;

import java.util.List;

@Builder
public record GetPropertiesResponseDTO(
        List<GetPropertyResponseDTO> properties
) {
}
