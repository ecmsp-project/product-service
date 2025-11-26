package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.rest.variant.GetVariantResponseDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class VariantController {
    private final VariantService variantService;
    private final VariantPropertyService variantPropertyService;

    public VariantController(
            VariantService variantService,
            VariantPropertyService variantPropertyService
    ) {
        this.variantService = variantService;
        this.variantPropertyService = variantPropertyService;
    }

    @GetMapping("/variant/{variantId}/details")
    public ResponseEntity<GetVariantResponseDTO> getAllVariantDetails(
            @PathVariable(required = true) UUID variantId
    ) {
        VariantResponseDTO variantResponse = variantService.getVariantById(variantId);
        List<VariantResponseDTO> otherVariantsResponse = variantService.getVariantsByProductId(variantResponse.getProductId());

        GetVariantResponseDTO response = GetVariantResponseDTO.builder()
                .variant(variantResponse)
                .otherVariantsWithImageURL(
                        otherVariantsResponse.stream().collect(Collectors.toMap(VariantResponseDTO::getId, VariantResponseDTO::getImageUrl))
                )
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<GetVariantResponseDTO> getVariantDetails(
            @PathVariable(required = true) UUID variantId
    ) {
        VariantResponseDTO variantResponse = variantService.getVariantById(variantId);
        List<VariantResponseDTO> otherVariantsResponse = variantService.getVariantsByProductId(variantResponse.getProductId());

        GetVariantResponseDTO response = GetVariantResponseDTO.builder()
                .variant(variantResponse)
                .build();
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
