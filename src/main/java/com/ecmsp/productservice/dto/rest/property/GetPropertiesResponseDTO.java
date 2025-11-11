package com.ecmsp.productservice.dto.rest.property;

import java.util.List;

public record GetPropertiesResponseDTO(
        List<GetPropertyResponseDTO> properties
) {
}
