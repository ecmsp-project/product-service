package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.ProductRequestDTO;
import com.ecmsp.productservice.dto.ProductResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private ProductResponseDTO convertToDto(Product product) {
        ProductResponseDTO.ProductResponseDTOBuilder dtoBuilder = ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .approximatePrice(product.getApproximatePrice())
                .deliveryPrice(product.getDeliveryPrice())
                .description(product.getDescription())
                .info(product.getInfo());

        if (product.getCategory() != null) {
            dtoBuilder.categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName());
        }
        return dtoBuilder.build();
    }

    private Product convertToEntity(ProductRequestDTO productRequestDTO) {
        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", productRequestDTO.getCategoryId()));

        return Product.builder()
                .name(productRequestDTO.getName())
                .approximatePrice(productRequestDTO.getApproximatePrice())
                .deliveryPrice(productRequestDTO.getDeliveryPrice())
                .description(productRequestDTO.getDescription())
                .info(productRequestDTO.getInfo())
                .category(category)
                .build();
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return convertToDto(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = convertToEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setApproximatePrice(productRequestDTO.getApproximatePrice());
        existingProduct.setDeliveryPrice(productRequestDTO.getDeliveryPrice());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setInfo(productRequestDTO.getInfo());

        if (!existingProduct.getCategory().getId().equals(productRequestDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findById(productRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", productRequestDTO.getCategoryId()));
            existingProduct.setCategory(newCategory);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }
}
