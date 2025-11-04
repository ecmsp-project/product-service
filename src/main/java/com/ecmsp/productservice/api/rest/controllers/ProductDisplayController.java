package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.rest.GetProductsRequestDTO;
import com.ecmsp.productservice.dto.rest.GetProductsResponseDTO;
import com.ecmsp.productservice.service.ProductDisplayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/")
public class ProductDisplayController {
    private final ProductDisplayService productDisplayService;

    public ProductDisplayController(
            ProductDisplayService productDisplayService
    ) {
        this.productDisplayService = productDisplayService;
    }

    @GetMapping("/products")
    public ResponseEntity<GetProductsResponseDTO> getProducts(@RequestBody GetProductsRequestDTO request) {
        GetProductsResponseDTO response = productDisplayService.getProducts(request);
        return ResponseEntity.ok(response);
    }
}
