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
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setApproximatePrice(product.getApproximatePrice());
        dto.setDeliveryPrice(product.getDeliveryPrice());
        dto.setDescription(product.getDescription());
        dto.setInfo(product.getInfo());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }

    private Product convertToEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setApproximatePrice(productRequestDTO.getApproximatePrice());
        product.setDeliveryPrice(productRequestDTO.getDeliveryPrice());
        product.setDescription(productRequestDTO.getDescription());
        product.setInfo(productRequestDTO.getInfo());

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", productRequestDTO.getCategoryId()));
        product.setCategory(category);

        return product;
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
