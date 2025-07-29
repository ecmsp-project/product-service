package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.dto.VariantRequestDTO;
import com.ecmsp.productservice.dto.VariantResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.VariantRepository;
import com.ecmsp.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VariantService {

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public VariantService(VariantRepository variantRepository, ProductRepository productRepository) {
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    private VariantResponseDTO convertToDto(Variant variant) {
        VariantResponseDTO.VariantResponseDTOBuilder dtoBuilder = VariantResponseDTO.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .imageUrl(variant.getImageUrl())
                .additionalAttributes(variant.getAdditionalAttributes())
                .description(variant.getDescription())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt());

        if (variant.getProduct() != null) {
            dtoBuilder.productId(variant.getProduct().getId())
                    .productName(variant.getProduct().getName());
        }

        dtoBuilder.variantAttributeCount(variant.getVariantAttributes() != null ? variant.getVariantAttributes().size() : 0);

        return dtoBuilder.build();
    }

    private Variant convertToEntity(VariantRequestDTO variantRequestDTO) {
        Product product = productRepository.findById(variantRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", variantRequestDTO.getProductId()));

        LocalDateTime now = LocalDateTime.now();

        return Variant.builder()
                .sku(variantRequestDTO.getSku())
                .price(variantRequestDTO.getPrice())
                .stockQuantity(variantRequestDTO.getStockQuantity())
                .imageUrl(variantRequestDTO.getImageUrl())
                .additionalAttributes(variantRequestDTO.getAdditionalAttributes())
                .description(variantRequestDTO.getDescription())
                .product(product)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public List<VariantResponseDTO> getAllVariants() {
        return variantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VariantResponseDTO getVariantById(UUID id) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));
        return convertToDto(variant);
    }

    @Transactional
    public VariantResponseDTO createVariant(VariantRequestDTO variantRequestDTO) {
        Variant variant = convertToEntity(variantRequestDTO);
        Variant savedVariant = variantRepository.save(variant);
        return convertToDto(savedVariant);
    }

    @Transactional
    public VariantResponseDTO updateVariant(UUID id, VariantRequestDTO variantRequestDTO) {
        Variant existingVariant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));

        existingVariant.setSku(variantRequestDTO.getSku());
        existingVariant.setPrice(variantRequestDTO.getPrice());
        existingVariant.setStockQuantity(variantRequestDTO.getStockQuantity());
        existingVariant.setImageUrl(variantRequestDTO.getImageUrl());
        existingVariant.setAdditionalAttributes(variantRequestDTO.getAdditionalAttributes());
        existingVariant.setDescription(variantRequestDTO.getDescription());
        existingVariant.setUpdatedAt(LocalDateTime.now());

        if (!existingVariant.getProduct().getId().equals(variantRequestDTO.getProductId())) {
            Product newProduct = productRepository.findById(variantRequestDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", variantRequestDTO.getProductId()));
            existingVariant.setProduct(newProduct);
        }

        Variant updatedVariant = variantRepository.save(existingVariant);
        return convertToDto(updatedVariant);
    }

    @Transactional
    public void deleteVariant(UUID id) {
        if (!variantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Variant", id);
        }
        variantRepository.deleteById(id);
    }
}