package com.ecmsp.productservice.service;

import com.ecmsp.productservice.dto.variant.VariantCreateRequestDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant_image.VariantImagesCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VariantOrchestratorService {

    private final VariantService variantService;
    private final VariantImageService variantImageService;
    private final VariantPropertyService variantPropertyService;

    public VariantOrchestratorService(
            VariantService variantService,
            VariantImageService variantImageService,
            VariantPropertyService variantPropertyService) {
        this.variantService = variantService;
        this.variantImageService = variantImageService;
        this.variantPropertyService = variantPropertyService;
    }

    @Transactional
    public UUID createFullVariant(
            VariantCreateRequestDTO variantRequest,
            VariantImagesCreateRequestDTO imagesRequest,
            List<VariantPropertyCreateRequestDTO> propertiesRequest
    ) {
        VariantResponseDTO variant = variantService.createVariant(variantRequest);

        variantImageService.createVariantImages(imagesRequest, variant.getId());

        for (VariantPropertyCreateRequestDTO pr : propertiesRequest) {
            pr.setVariantId(variant.getId());
            variantPropertyService.createVariantProperty(pr);
        }

        return variant.getId();
    }
}
