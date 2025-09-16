package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.product.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private ProductResponseDTO convertToDto(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .approximatePrice(product.getApproximatePrice())
                .deliveryPrice(product.getDeliveryPrice())
                .description(product.getDescription())
                .info(product.getInfo())
                .categoryId(product.getCategory().getId())
                .build();

    }

    private Product convertToEntity(ProductCreateRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        return Product.builder()
                .name(request.getName())
                .approximatePrice(request.getApproximatePrice())
                .deliveryPrice(request.getDeliveryPrice())
                .description(request.getDescription())
                .info(request.getInfo())
                .category(category)
                .build();
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public ProductResponseDTO getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

    }

    @Transactional
    public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO request) {
        Product product = convertToEntity(request);
        Product savedProduct = productRepository.save(product);

        return ProductCreateResponseDTO
                .builder()
                .id(savedProduct.getId())
                .build();
    }

    @Transactional
    public ProductResponseDTO updateProduct(UUID id, ProductUpdateRequestDTO request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }
        if (request.getApproximatePrice() != null) {
            existingProduct.setApproximatePrice(request.getApproximatePrice());
        }
        if (request.getDeliveryPrice() != null) {
            existingProduct.setDeliveryPrice(request.getDeliveryPrice());
        }
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription());
        }
        if (request.getInfo() != null) {
            existingProduct.setInfo(request.getInfo());
        }

        UUID newCategoryId = request.getCategoryId();
        UUID currentCategoryId = existingProduct.getCategory().getId();

        if (!newCategoryId.equals(currentCategoryId)) {
            Category category = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", newCategoryId));
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
    }
}
