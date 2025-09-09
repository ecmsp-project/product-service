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

import java.math.BigDecimal;
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
        if (variantRequestDTO.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (variantRequestDTO.getSku() == null || variantRequestDTO.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU cannot be blank.");
        }
        if (variantRequestDTO.getSku().length() > 12) {
            throw new IllegalArgumentException("SKU cannot exceed 12 characters.");
        }
        if (variantRequestDTO.getPrice() == null) {
            throw new IllegalArgumentException("Price is required.");
        }
        if (variantRequestDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        if (variantRequestDTO.getStockQuantity() == null || variantRequestDTO.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (variantRequestDTO.getImageUrl() == null || variantRequestDTO.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be blank.");
        }
        if (variantRequestDTO.getAdditionalAttributes() == null) {
            throw new IllegalArgumentException("Additional attributes are required.");
        }
        if (variantRequestDTO.getDescription() == null || variantRequestDTO.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        Variant variant = convertToEntity(variantRequestDTO);
        Variant savedVariant = variantRepository.save(variant);
        return convertToDto(savedVariant);
    }

    @Transactional
    public VariantResponseDTO updateVariant(UUID id, VariantRequestDTO variantRequestDTO) {
        Variant existingVariant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));

        if (variantRequestDTO.getSku() != null) {
            if (variantRequestDTO.getSku().isBlank()) {
                throw new IllegalArgumentException("SKU cannot be blank.");
            }
            if (variantRequestDTO.getSku().length() > 12) {
                throw new IllegalArgumentException("SKU cannot exceed 12 characters.");
            }
            existingVariant.setSku(variantRequestDTO.getSku());
        }

        if (variantRequestDTO.getPrice() != null) {
            if (variantRequestDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be positive.");
            }
            existingVariant.setPrice(variantRequestDTO.getPrice());
        }

        if (variantRequestDTO.getStockQuantity() != null) {
            if (variantRequestDTO.getStockQuantity() < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative.");
            }
            existingVariant.setStockQuantity(variantRequestDTO.getStockQuantity());
        }

        if (variantRequestDTO.getImageUrl() != null) {
            if (variantRequestDTO.getImageUrl().isBlank()) {
                throw new IllegalArgumentException("Image URL cannot be blank.");
            }
            existingVariant.setImageUrl(variantRequestDTO.getImageUrl());
        }

        if (variantRequestDTO.getAdditionalAttributes() != null) {
            existingVariant.setAdditionalAttributes(variantRequestDTO.getAdditionalAttributes());
        }

        if (variantRequestDTO.getDescription() != null) {
            if (variantRequestDTO.getDescription().isBlank()) {
                throw new IllegalArgumentException("Description cannot be blank.");
            }
            existingVariant.setDescription(variantRequestDTO.getDescription());
        }

        if (variantRequestDTO.getProductId() != null) {
            UUID newProductId = variantRequestDTO.getProductId();
            UUID currentProductId = existingVariant.getProduct().getId();
            if (!newProductId.equals(currentProductId)) {
                Product newProduct = productRepository.findById(newProductId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", newProductId));
                existingVariant.setProduct(newProduct);
            }
        }

        existingVariant.setUpdatedAt(LocalDateTime.now());
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