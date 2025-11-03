package com.ecmsp.productservice.dto.rest;

import java.util.UUID;

public record GetProductsRequestDTO(
        UUID categoryId,
        String categoryName,
        String pageToken,

        Integer pageSize
) {
}
