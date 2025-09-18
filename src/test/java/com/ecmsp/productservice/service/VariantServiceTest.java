package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.dto.variant.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.repository.VariantRepository;
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
class VariantServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private VariantService variantService;

    private Category testCategory;
    private Product testProduct;
    private Variant testVariant;
    private UUID categoryId;
    private UUID productId;
    private UUID variantId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        productId = UUID.randomUUID();
        variantId = UUID.randomUUID();

        testCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .build();

        testProduct = TestEntitiesBuilder.productBuilder()
                .id(productId)
                .category(testCategory)
                .build();

        testVariant = TestEntitiesBuilder.variantBuilder()
                .id(variantId)
                .product(testProduct)
                .build();
    }

    @Test
    void getAllVariants_ShouldReturnListOfVariantResponseDTO() {
        // Given
        when(variantRepository.findAll()).thenReturn(List.of(testVariant));

        // When
        List<VariantResponseDTO> result = variantService.getAllVariants();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(variantId);
        assertThat(result.get(0).getProductId()).isEqualTo(productId);
        assertThat(result.get(0).getPrice()).isEqualTo(testVariant.getPrice());
        assertThat(result.get(0).getStockQuantity()).isEqualTo(testVariant.getStockQuantity());
        verify(variantRepository).findAll();
    }

    @Test
    void getVariantById_WhenVariantExists_ShouldReturnVariantResponseDTO() {
        // Given
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));

        // When
        VariantResponseDTO result = variantService.getVariantById(variantId);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(testVariant.getPrice());
        verify(variantRepository).findById(variantId);
    }

    @Test
    void getVariantById_WhenVariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantService.getVariantById(variantId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Variant");
        verify(variantRepository).findById(variantId);
    }

    @Test
    void createVariant_WhenProductExists_ShouldReturnVariantResponseDTO() {
        // Given
        VariantCreateRequestDTO request = VariantCreateRequestDTO.builder()
                .productId(productId)
                .price(new BigDecimal("99.99"))
                .stockQuantity(15)
                .imageUrl("http://example.com/new-image.jpg")
                .description("New variant description")
                .additionalProperties(Map.of("color", "blue"))
                .build();

        Variant savedVariant = TestEntitiesBuilder.variantBuilder()
                .id(variantId)
                .product(testProduct)
                .price(new BigDecimal("99.99"))
                .stockQuantity(15)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(variantRepository.save(any(Variant.class))).thenReturn(savedVariant);

        // When
        VariantResponseDTO result = variantService.createVariant(request);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(result.getStockQuantity()).isEqualTo(15);
        verify(productRepository).findById(productId);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test
    void createVariant_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        VariantCreateRequestDTO request = VariantCreateRequestDTO.builder()
                .productId(productId)
                .price(new BigDecimal("99.99"))
                .stockQuantity(15)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantService.createVariant(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
        verify(productRepository).findById(productId);
        verify(variantRepository, never()).save(any());
    }

    @Test
    void updateVariant_WhenVariantExists_ShouldUpdateAllFields() {
        // Given
        UUID newProductId = UUID.randomUUID();
        Product newProduct = TestEntitiesBuilder.productBuilder()
                .id(newProductId)
                .category(testCategory)
                .build();

        VariantUpdateRequestDTO request = VariantUpdateRequestDTO.builder()
                .price(new BigDecimal("149.99"))
                .stockQuantity(25)
                .imageUrl("http://example.com/updated-image.jpg")
                .description("Updated variant description")
                .additionalProperties(Map.of("color", "red", "size", "large"))
                .productId(newProductId)
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(newProduct));
        when(variantRepository.save(any(Variant.class))).thenReturn(testVariant);

        // When
        VariantResponseDTO result = variantService.updateVariant(variantId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        verify(variantRepository).findById(variantId);
        verify(productRepository).findById(newProductId);
        verify(variantRepository).save(testVariant);
    }

    @Test
    void updateVariant_WhenVariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        VariantUpdateRequestDTO request = VariantUpdateRequestDTO.builder()
                .price(new BigDecimal("149.99"))
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantService.updateVariant(variantId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Variant");
        verify(variantRepository).findById(variantId);
        verify(variantRepository, never()).save(any());
    }

    @Test
    void updateVariant_WhenNewProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        UUID newProductId = UUID.randomUUID();
        VariantUpdateRequestDTO request = VariantUpdateRequestDTO.builder()
                .productId(newProductId)
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(productRepository.findById(newProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantService.updateVariant(variantId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
        verify(variantRepository).findById(variantId);
        verify(productRepository).findById(newProductId);
        verify(variantRepository, never()).save(any());
    }

    @Test
    void updateVariant_WhenProductIdIsSame_ShouldNotUpdateProduct() {
        // Given
        VariantUpdateRequestDTO request = VariantUpdateRequestDTO.builder()
                .price(new BigDecimal("149.99"))
                .productId(productId) // Same product
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(variantRepository.save(any(Variant.class))).thenReturn(testVariant);

        // When
        VariantResponseDTO result = variantService.updateVariant(variantId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        verify(variantRepository).findById(variantId);
        verify(productRepository, never()).findById(any()); // Should not fetch product
        verify(variantRepository).save(testVariant);
    }

    @Test
    void updateVariant_WithNullFields_ShouldNotUpdateNullFields() {
        // Given
        VariantUpdateRequestDTO request = VariantUpdateRequestDTO.builder()
                .price(new BigDecimal("149.99"))
                // other fields are null
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(variantRepository.save(any(Variant.class))).thenReturn(testVariant);

        // When
        VariantResponseDTO result = variantService.updateVariant(variantId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(testVariant);
    }

    @Test
    void deleteVariant_WhenVariantExists_ShouldDeleteVariant() {
        // Given
        when(variantRepository.existsById(variantId)).thenReturn(true);

        // When
        variantService.deleteVariant(variantId);

        // Then
        verify(variantRepository).existsById(variantId);
        verify(variantRepository).deleteById(variantId);
    }

    @Test
    void deleteVariant_WhenVariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(variantRepository.existsById(variantId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> variantService.deleteVariant(variantId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Variant");
        verify(variantRepository).existsById(variantId);
        verify(variantRepository, never()).deleteById(any());
    }

    @Test
    void createVariant_WithMinimalData_ShouldCreateSuccessfully() {
        // Given
        VariantCreateRequestDTO request = VariantCreateRequestDTO.builder()
                .productId(productId)
                .price(new BigDecimal("50.00"))
                .stockQuantity(5)
                .imageUrl("http://example.com/minimal.jpg")
                .build();

        Variant savedVariant = TestEntitiesBuilder.variantBuilder()
                .id(variantId)
                .product(testProduct)
                .price(new BigDecimal("50.00"))
                .stockQuantity(5)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(variantRepository.save(any(Variant.class))).thenReturn(savedVariant);

        // When
        VariantResponseDTO result = variantService.createVariant(request);

        // Then
        assertThat(result.getId()).isEqualTo(variantId);
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(result.getStockQuantity()).isEqualTo(5);
        verify(productRepository).findById(productId);
        verify(variantRepository).save(any(Variant.class));
    }
}