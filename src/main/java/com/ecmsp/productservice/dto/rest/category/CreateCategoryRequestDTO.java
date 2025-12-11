package com.ecmsp.productservice.dto.rest.category;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryRequestDTO(
        UUID parentCategoryId,
        UUID childCategoryId,
        String name
) {
}
