package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantImage;
import com.ecmsp.productservice.dto.variant_image.VariantImageCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_image.VariantImageResponseDTO;
import com.ecmsp.productservice.dto.variant_image.VariantImagesCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_image.VariantImagesCreateResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.VariantImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class VariantImageService {
    private final VariantImageRepository variantImageRepository;
    private final VariantService variantService;

    public VariantImageService(
            VariantImageRepository variantImageRepository,
            VariantService variantService
    ) {
        this.variantImageRepository = variantImageRepository;
        this.variantService = variantService;
    }


    public VariantImageResponseDTO convertToDto(VariantImage variantImage) {
        return VariantImageResponseDTO.builder()
                .id(variantImage.getId())
                .url(variantImage.getUrl())
                .variantId(variantImage.getVariant().getId())
                .position(variantImage.getPosition())
                .build();
    }

    public List<VariantImageResponseDTO> convertVariantImagesToDto(List<VariantImage> variantImages) {
        return variantImages.stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(VariantImageResponseDTO::position))
                .toList();
    }

    public List<VariantImage> getVariantImagesByVariantId(UUID variantId) {
        return variantImageRepository.findByVariantId(variantId);
    }

    @Transactional
    public VariantImageResponseDTO createVariantImage(VariantImageCreateRequestDTO request, Integer position) {
        Variant variant = variantService.getVariantEntityById(request.variantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        VariantImage variantImage = VariantImage.builder()
                .variant(variant)
                .url(request.url())
                .position(position)
                .build();

        VariantImage savedVariantImage = variantImageRepository.save(variantImage);
        return convertToDto(savedVariantImage);
    }

    @Transactional
    public VariantImagesCreateResponseDTO createVariantImages(VariantImagesCreateRequestDTO request, UUID variantId) {
        List<UUID> variantImagesIds = new ArrayList<>();

        for (int position = 0; position < request.variantImages().size(); position++) {
            String url = request.variantImages().get(position);

            VariantImageCreateRequestDTO variantImageCreateRequest = VariantImageCreateRequestDTO.builder()
                    .url(url)
                    .variantId(variantId)
                    .build();

            VariantImageResponseDTO variantImageResponse = createVariantImage(variantImageCreateRequest, position);
            variantImagesIds.add(variantImageResponse.id());
        }

        return VariantImagesCreateResponseDTO.builder()
                .variantImageIds(variantImagesIds)
                .build();
    }
}
