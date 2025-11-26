package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.VariantImage;
import com.ecmsp.productservice.dto.VariantImageResponseDTO;
import com.ecmsp.productservice.dto.property.PropertyResponseDTO;
import com.ecmsp.productservice.repository.VariantImageRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class VariantImageService {
    private final VariantImageRepository variantImageRepository;

    public VariantImageService(VariantImageRepository variantImageRepository) {
        this.variantImageRepository = variantImageRepository;
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
}
