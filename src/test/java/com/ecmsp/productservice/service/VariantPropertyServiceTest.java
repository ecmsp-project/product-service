package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.*;
import com.ecmsp.productservice.dto.variant_property.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.*;
import com.ecmsp.productservice.util.TestEntitiesBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantPropertyServiceTest {

    @Mock
    private VariantPropertyRepository variantPropertyRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private DefaultPropertyOptionRepository defaultPropertyOptionRepository;

    @InjectMocks
    private VariantPropertyService variantPropertyService;

    private Category testCategory;
    private Product testProduct;
    private Variant testVariant;
    private Property textProperty;
    private Property numberProperty;
    private Property booleanProperty;
    private Property dateProperty;
    private VariantProperty testVariantProperty;
    private UUID categoryId;
    private UUID productId;
    private UUID variantId;
    private UUID propertyId;
    private UUID variantPropertyId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        productId = UUID.randomUUID();
        variantId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        variantPropertyId = UUID.randomUUID();

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

        textProperty = TestEntitiesBuilder.propertyBuilder()
                .id(propertyId)
                .category(testCategory)
                .dataType(PropertyDataType.TEXT)
                .hasDefaultOptions(false)
                .build();

        numberProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.NUMBER)
                .hasDefaultOptions(false)
                .build();

        booleanProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.BOOLEAN)
                .hasDefaultOptions(false)
                .build();

        dateProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.DATE)
                .hasDefaultOptions(false)
                .build();

        testVariantProperty = TestEntitiesBuilder.textVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(textProperty)
                .build();
    }

    @Test
    void getAllVariantProperties_ShouldReturnListOfVariantPropertyResponseDTO() {
        // Given
        when(variantPropertyRepository.findAll()).thenReturn(List.of(testVariantProperty));

        // When
        List<VariantPropertyResponseDTO> result = variantPropertyService.getAllVariantProperties();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(variantPropertyId);
        assertThat(result.get(0).getVariantId()).isEqualTo(variantId);
        assertThat(result.get(0).getPropertyId()).isEqualTo(propertyId);
        assertThat(result.get(0).getPropertyDataType()).isEqualTo(PropertyDataType.TEXT);
        verify(variantPropertyRepository).findAll();
    }

    @Test
    void getVariantPropertyById_WhenVariantPropertyExists_ShouldReturnVariantPropertyResponseDTO() {
        // Given
        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(testVariantProperty));

        // When
        VariantPropertyResponseDTO result = variantPropertyService.getVariantPropertyById(variantPropertyId);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        assertThat(result.getVariantId()).isEqualTo(variantId);
        assertThat(result.getPropertyId()).isEqualTo(propertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
    }

    @Test
    void getVariantPropertyById_WhenVariantPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.getVariantPropertyById(variantPropertyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("VariantProperty");
        verify(variantPropertyRepository).findById(variantPropertyId);
    }

    @Test
    void createVariantProperty_WhenVariantAndPropertyExist_ShouldReturnVariantPropertyCreateResponseDTO() {
        // Given
        VariantPropertyCreateRequestDTO request = VariantPropertyCreateRequestDTO.builder()
                .variantId(variantId)
                .propertyId(propertyId)
                .build();

        VariantProperty savedVariantProperty = TestEntitiesBuilder.textVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(textProperty)
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(textProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(savedVariantProperty);

        // When
        VariantPropertyCreateResponseDTO result = variantPropertyService.createVariantProperty(request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantRepository).findById(variantId);
        verify(propertyRepository).findById(propertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void createVariantProperty_WhenVariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        VariantPropertyCreateRequestDTO request = VariantPropertyCreateRequestDTO.builder()
                .variantId(variantId)
                .propertyId(propertyId)
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.createVariantProperty(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Variant");
        verify(variantRepository).findById(variantId);
        verify(variantPropertyRepository, never()).save(any());
    }

    @Test
    void createVariantProperty_WhenPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        VariantPropertyCreateRequestDTO request = VariantPropertyCreateRequestDTO.builder()
                .variantId(variantId)
                .propertyId(propertyId)
                .build();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.createVariantProperty(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        verify(variantRepository).findById(variantId);
        verify(propertyRepository).findById(propertyId);
        verify(variantPropertyRepository, never()).save(any());
    }

    @Test
    void updateVariantProperty_ForTextProperty_ShouldUpdateSuccessfully() {
        // Given
        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueText("Updated Text Value")
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(testVariantProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(testVariantProperty);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void updateVariantProperty_ForNumberProperty_ShouldUpdateSuccessfully() {
        // Given
        VariantProperty numberVariantProperty = TestEntitiesBuilder.numberVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(numberProperty)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Number Display")
                .valueDecimal(new BigDecimal("999.99"))
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(numberVariantProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(numberVariantProperty);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void updateVariantProperty_ForBooleanProperty_ShouldUpdateSuccessfully() {
        // Given
        VariantProperty booleanVariantProperty = TestEntitiesBuilder.booleanVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(booleanProperty)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Boolean Display")
                .valueBoolean(true)
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(booleanVariantProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(booleanVariantProperty);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void updateVariantProperty_ForDateProperty_ShouldUpdateSuccessfully() {
        // Given
        VariantProperty dateVariantProperty = TestEntitiesBuilder.dateVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(dateProperty)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Date Display")
                .valueDate(LocalDate.of(2025, 12, 25))
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(dateVariantProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(dateVariantProperty);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void updateVariantProperty_WhenVariantPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueText("Updated Text")
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.updateVariantProperty(variantPropertyId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("VariantProperty");
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository, never()).save(any());
    }

    @Test
    void updateVariantProperty_ForTextPropertyWithMissingValue_ShouldThrowIllegalArgumentException() {
        // Given
        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueText(null) // Missing required value for TEXT property
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(testVariantProperty));

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.updateVariantProperty(variantPropertyId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Text value is required for TEXT data type");
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository, never()).save(any());
    }

    @Test
    void updateVariantProperty_ForNumberPropertyWithMissingValue_ShouldThrowIllegalArgumentException() {
        // Given
        VariantProperty numberVariantProperty = TestEntitiesBuilder.numberVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(numberProperty)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueDecimal(null) // Missing required value for NUMBER property
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(numberVariantProperty));

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.updateVariantProperty(variantPropertyId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Decimal value is required for NUMBER data type");
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository, never()).save(any());
    }

    @Test
    void updateVariantProperty_WithPropertyHavingDefaultOptions_ShouldUpdateWithoutValidation() {
        // Given
        Property propertyWithDefaults = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.TEXT)
                .hasDefaultOptions(true)
                .build();

        VariantProperty variantPropertyWithDefaults = TestEntitiesBuilder.textVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(propertyWithDefaults)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .displayText("Custom Display Text")
                .valueText("Custom Value")
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(variantPropertyWithDefaults));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(variantPropertyWithDefaults);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }

    @Test
    void deleteVariantProperty_WhenVariantPropertyExists_ShouldDeleteVariantProperty() {
        // Given
        when(variantPropertyRepository.existsById(variantPropertyId)).thenReturn(true);

        // When
        variantPropertyService.deleteVariantProperty(variantPropertyId);

        // Then
        verify(variantPropertyRepository).existsById(variantPropertyId);
        verify(variantPropertyRepository).deleteById(variantPropertyId);
    }

    @Test
    void deleteVariantProperty_WhenVariantPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(variantPropertyRepository.existsById(variantPropertyId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> variantPropertyService.deleteVariantProperty(variantPropertyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("VariantProperty");
        verify(variantPropertyRepository).existsById(variantPropertyId);
        verify(variantPropertyRepository, never()).deleteById(any());
    }

    @Test
    void updateVariantProperty_WithoutExplicitDisplayText_ShouldGenerateDisplayText() {
        // Given
        VariantProperty numberVariantProperty = TestEntitiesBuilder.numberVariantPropertyBuilder()
                .id(variantPropertyId)
                .variant(testVariant)
                .property(numberProperty)
                .build();

        VariantPropertyUpdateRequestDTO request = VariantPropertyUpdateRequestDTO.builder()
                .valueDecimal(new BigDecimal("123.45"))
                // No explicit displayText provided
                .build();

        when(variantPropertyRepository.findById(variantPropertyId)).thenReturn(Optional.of(numberVariantProperty));
        when(variantPropertyRepository.save(any(VariantProperty.class))).thenReturn(numberVariantProperty);

        // When
        VariantPropertyResponseDTO result = variantPropertyService.updateVariantProperty(variantPropertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(variantPropertyId);
        verify(variantPropertyRepository).findById(variantPropertyId);
        verify(variantPropertyRepository).save(any(VariantProperty.class));
    }
}