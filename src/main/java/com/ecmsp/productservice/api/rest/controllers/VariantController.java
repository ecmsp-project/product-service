package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.dto.rest.variant.GetVariantRequestResponseDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.service.VariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class VariantController {
    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<GetVariantRequestResponseDTO> getVariantDetails(
            @PathVariable(required = false) UUID variantId
    ) {
        VariantResponseDTO variantResponse = variantService.getVariantById(variantId);
        List<VariantResponseDTO> otherVariantsResponse = variantService.getOtherVariantsIds(variantResponse.getProductId(), variantId);

        GetVariantRequestResponseDTO response = GetVariantRequestResponseDTO.builder()
                .variant(variantResponse)
                .otherVariantsWithImageURL(
                        otherVariantsResponse.stream().collect(Collectors.toMap(VariantResponseDTO::getId, VariantResponseDTO::getImageUrl))
                )
                .build();
        return ResponseEntity.ok(response);
    }
}
