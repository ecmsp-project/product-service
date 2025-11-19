package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.rest.GetProductsRequestDTO;
import com.ecmsp.productservice.dto.rest.GetProductsResponseDTO;
import com.ecmsp.productservice.dto.rest.product.GetProductsFilteredRequestDTO;
import com.ecmsp.productservice.service.ProductDisplayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductDisplayController {
    private final ProductDisplayService productDisplayService;

    public ProductDisplayController(
            ProductDisplayService productDisplayService
    ) {
        this.productDisplayService = productDisplayService;
    }

    @PostMapping("/products")
    public ResponseEntity<GetProductsResponseDTO> getProducts(
            @RequestBody GetProductsRequestDTO request,
            @RequestParam(required = true) UUID categoryId
    ) {
        GetProductsResponseDTO response = productDisplayService.getProducts(request, categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/filter")
    public ResponseEntity<GetProductsResponseDTO> getProductsFiltered(
            @RequestBody GetProductsFilteredRequestDTO request
    ) {
        GetProductsResponseDTO response = productDisplayService.getProductsFiltered(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/search")
    public ResponseEntity<GetProductsResponseDTO> searchProducts(
            @RequestBody GetProductsRequestDTO request,
            @RequestParam(required = true) String query
    ) {
        GetProductsResponseDTO response = productDisplayService.getProductsQueried(request, query);
        return ResponseEntity.ok(response);
    }
}
