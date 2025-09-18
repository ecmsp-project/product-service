package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.dto.product.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.CategoryRepository;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.util.TestEntitiesBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;
    private UUID categoryId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        productId = UUID.randomUUID();

        testCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .build();

        testProduct = TestEntitiesBuilder.productBuilder()
                .id(productId)
                .category(testCategory)
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductResponseDTO() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(productId);
        assertThat(result.get(0).getName()).isEqualTo(testProduct.getName());
        assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId);
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProductResponseDTO() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponseDTO result = productService.getProductById(productId);

        // Then
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo(testProduct.getName());
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
        verify(productRepository).findById(productId);
    }

    @Test
    void createProduct_WhenCategoryExists_ShouldReturnProductCreateResponseDTO() {
        // Given
        ProductCreateRequestDTO request = ProductCreateRequestDTO.builder()
                .name("New Product")
                .approximatePrice(new BigDecimal("199.99"))
                .deliveryPrice(new BigDecimal("19.99"))
                .description("New product description")
                .categoryId(categoryId)
                .info(new HashMap<>())
                .build();

        Product savedProduct = TestEntitiesBuilder.productBuilder()
                .id(productId)
                .name("New Product")
                .category(testCategory)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductCreateResponseDTO result = productService.createProduct(request);

        // Then
        assertThat(result.getId()).isEqualTo(productId);
        verify(categoryRepository).findById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        ProductCreateRequestDTO request = ProductCreateRequestDTO.builder()
                .name("New Product")
                .categoryId(categoryId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(categoryRepository).findById(categoryId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAllFields() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        Category newCategory = TestEntitiesBuilder.categoryBuilder()
                .id(newCategoryId)
                .build();

        ProductUpdateRequestDTO request = ProductUpdateRequestDTO.builder()
                .name("Updated Product")
                .approximatePrice(new BigDecimal("299.99"))
                .deliveryPrice(new BigDecimal("29.99"))
                .description("Updated description")
                .categoryId(newCategoryId)
                .info(Map.of("key", "value"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponseDTO result = productService.updateProduct(productId, request);

        // Then
        assertThat(result.getId()).isEqualTo(productId);
        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(newCategoryId);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        ProductUpdateRequestDTO request = ProductUpdateRequestDTO.builder()
                .name("Updated Product")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(productId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenNewCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        ProductUpdateRequestDTO request = ProductUpdateRequestDTO.builder()
                .categoryId(newCategoryId)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(productId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(newCategoryId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenCategoryIdIsSame_ShouldNotUpdateCategory() {
        // Given
        ProductUpdateRequestDTO request = ProductUpdateRequestDTO.builder()
                .name("Updated Product")
                .categoryId(categoryId) // Same category
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponseDTO result = productService.updateProduct(productId, request);

        // Then
        assertThat(result.getId()).isEqualTo(productId);
        verify(productRepository).findById(productId);
        verify(categoryRepository, never()).findById(any()); // Should not fetch category
        verify(productRepository).save(testProduct);
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).findById(productId);
        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }
}