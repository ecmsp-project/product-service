package com.ecmsp.productservice.dto.rest.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetProductsFilteredRequestDTO(
        String categoryId,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        // List of default property options ids that the variants must satisfy
        List<UUID> defaultPropertyOptionIds,

        Integer pageNumber,
        Integer pageSize
) {
}
