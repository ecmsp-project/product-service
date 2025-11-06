package com.ecmsp.productservice.dto.rest.products;

import java.math.BigDecimal;
import java.util.List;

public record GetProductsFilteredRequestDTO(
        String categoryId,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        List<DefaultPropertyFilter> defaultPropertyFilters
) {
}

record DefaultPropertyFilter(
        String defaultPropertyId,
        String value
) {
}