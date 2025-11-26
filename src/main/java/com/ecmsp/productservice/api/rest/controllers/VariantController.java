package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.rest.variant.GetVariantResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
import com.ecmsp.productservice.service.ProductDisplayService;
import com.ecmsp.productservice.service.PropertyService;
import com.ecmsp.productservice.service.VariantPropertyService;
import com.ecmsp.productservice.service.VariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class VariantController {
    private final VariantService variantService;
    private final VariantPropertyService variantPropertyService;
    private final PropertyService propertyService;
    private final ProductDisplayService productDisplayService;

    public VariantController(
            VariantService variantService,
            VariantPropertyService variantPropertyService,
            PropertyService propertyService,
            ProductDisplayService productDisplayService) {
        this.variantService = variantService;
        this.variantPropertyService = variantPropertyService;
        this.propertyService = propertyService;
        this.productDisplayService = productDisplayService;
    }

    @GetMapping("/variant/{variantId}/details")
    public ResponseEntity<GetVariantResponseDTO> getAllVariantDetails(
            @PathVariable(required = true) UUID variantId
    ) {

        GetVariantResponseDTO response = productDisplayService.getAllVariantDetails(variantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<GetVariantResponseDTO> getVariantDetails(
            @PathVariable(required = true) UUID variantId
    ) {

        GetVariantResponseDTO response = productDisplayService.getVariantDetails(variantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/variant/{variantId}/properties")
    public ResponseEntity<Map<String, List<VariantPropertyResponseDTO>>> getVariantProperties(
            @PathVariable(required = true) UUID variantId
    ) {
        Map<String, List<VariantPropertyResponseDTO>> response = variantPropertyService.getVariantPropertiesByVariantIdGrouped(variantId);
        return ResponseEntity.ok(response);
    }
}
