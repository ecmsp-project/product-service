
package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.dto.variant.VariantCreateRequestDTO;
import com.ecmsp.productservice.dto.variant.VariantRequestDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant.VariantUpdateRequestDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.VariantRepository;
import com.ecmsp.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Var;
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

                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .imageUrl(variant.getImageUrl())
                .additionalProperties(variant.getAdditionalProperties())
                .description(variant.getDescription())
                .productId(variant.getProduct().getId())

                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())

                .build();
    }

    private Variant convertToEntity(VariantCreateRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        return Variant.builder()
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .additionalProperties(request.getAdditionalProperties())
                .description(request.getDescription())
                .product(product)
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

    public List<Variant> getVariantsByProductId(UUID productId) {
        return variantRepository.findByProductId(productId);
    }

    public List<Variant> getOneVariantPerProductByCategoryId(UUID categoryId) {
        return variantRepository.getOneVariantPerProductByCategoryId(categoryId);
    }

    @Transactional
    public VariantResponseDTO createVariant(VariantCreateRequestDTO request) {
        Variant variant = convertToEntity(request);
        Variant savedVariant = variantRepository.save(variant);
        return convertToDto(savedVariant);
    }

    @Transactional
    public VariantResponseDTO updateVariant(UUID id, VariantUpdateRequestDTO request) {
        Variant existingVariant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));

        if (request.getPrice() != null) {
            existingVariant.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            existingVariant.setStockQuantity(request.getStockQuantity());
        }
        if (request.getImageUrl() != null) {
            existingVariant.setImageUrl(request.getImageUrl());
        }
        if (request.getAdditionalProperties() != null) {
            existingVariant.setAdditionalProperties(request.getAdditionalProperties());
        }
        if (request.getDescription() != null) {
            existingVariant.setDescription(request.getDescription());
        }

        if (request.getProductId() != null) {
            UUID newProductId = request.getProductId();
            UUID currentProductId = existingVariant.getProduct().getId();

            if (!newProductId.equals(currentProductId)) {
                Product newProduct = productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
                existingVariant.setProduct(newProduct);
            }
        }

        Variant updatedVariant = variantRepository.save(existingVariant);
        return convertToDto(updatedVariant);
    }

    @Transactional
    void reserveVariant(UUID variantId, int quantity) {
        variantRepository.reserveVariant(variantId, quantity);
    }

    @Transactional
    public void deleteVariant(UUID id) {
        if (!variantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Variant", id);
        }
        variantRepository.deleteById(id);
    }
}
