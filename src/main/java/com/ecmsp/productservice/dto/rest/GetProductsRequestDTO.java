package com.ecmsp.productservice.dto.rest;

public record GetProductsRequestDTO(
        Integer pageNumber,
        Integer pageSize
) {
}
