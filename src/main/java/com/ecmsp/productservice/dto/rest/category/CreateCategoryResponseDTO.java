package com.ecmsp.productservice.dto.rest.category;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryResponseDTO(
        UUID id
) {
}
