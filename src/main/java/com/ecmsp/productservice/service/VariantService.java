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
import java.util.Optional;
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
        return VariantResponseDTO.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .imageUrl(variant.getImageUrl())
                .additionalAttributes(variant.getAdditionalAttributes())
                .description(variant.getDescription())
                .productId(variant.getProduct().getId())
                .variantAttributeCount(variant.getVariantAttributes().size())
                .updatedAt(variant.getUpdatedAt())
                .build();
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
                .createdAt(now) // TODO: ->
                .updatedAt(now) // TODO: it is possible for Spring to automatically update times during creation or update
                .build();
    }

    public List<VariantResponseDTO> getAllVariants() {
        return variantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VariantResponseDTO getVariantById(UUID id) {
        return variantRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));
    }

    public Optional<Variant> getVariantEntityById(UUID id) {
        return variantRepository.findById(id);
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

        UUID newProductId = variantRequestDTO.getProductId();
        UUID currentProductId = existingVariant.getProduct().getId();

        if (!newProductId.equals(currentProductId)) {
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