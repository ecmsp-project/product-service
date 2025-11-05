package com.ecmsp.productservice.dto.rest;

import lombok.Builder;

import java.util.List;

@Builder
public record GetProductsResponseDTO(
        List<ProductRepresentationDTO> productsRepresentation,
        Integer nextPageNumber
) {

}
