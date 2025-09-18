package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.dto.property.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.CategoryRepository;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.util.TestEntitiesBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PropertyService propertyService;

    private Category testCategory;
    private Property testProperty;
    private UUID categoryId;
    private UUID propertyId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        testCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .build();

        testProperty = TestEntitiesBuilder.propertyBuilder()
                .id(propertyId)
                .category(testCategory)
                .dataType(PropertyDataType.TEXT)
                .build();
    }

    @Test
    void getAllProperties_ShouldReturnListOfPropertyResponseDTO() {
        // Given
        when(propertyRepository.findAll()).thenReturn(List.of(testProperty));

        // When
        List<PropertyResponseDTO> result = propertyService.getAllProperties();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(propertyId);
        assertThat(result.get(0).getName()).isEqualTo(testProperty.getName());
        assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId);
        assertThat(result.get(0).getDataType()).isEqualTo(PropertyDataType.TEXT);
        verify(propertyRepository).findAll();
    }

    @Test
    void getPropertyById_WhenPropertyExists_ShouldReturnPropertyResponseDTO() {
        // Given
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        // When
        PropertyResponseDTO result = propertyService.getPropertyById(propertyId);

        // Then
        assertThat(result.getId()).isEqualTo(propertyId);
        assertThat(result.getName()).isEqualTo(testProperty.getName());
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        assertThat(result.getDataType()).isEqualTo(PropertyDataType.TEXT);
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void getPropertyById_WhenPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyService.getPropertyById(propertyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void createProperty_WhenCategoryExists_ShouldReturnPropertyCreateResponseDTO() {
        // Given
        PropertyCreateRequestDTO request = PropertyCreateRequestDTO.builder()
                .name("New Property")
                .unit("kg")
                .dataType(PropertyDataType.NUMBER)
                .required(true)
                .categoryId(categoryId)
                .build();

        Property savedProperty = TestEntitiesBuilder.propertyBuilder()
                .id(propertyId)
                .name("New Property")
                .category(testCategory)
                .dataType(PropertyDataType.NUMBER)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        // When
        PropertyCreateResponseDTO result = propertyService.createProperty(request);

        // Then
        assertThat(result.getId()).isEqualTo(propertyId);
        verify(categoryRepository).findById(categoryId);
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void createProperty_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        PropertyCreateRequestDTO request = PropertyCreateRequestDTO.builder()
                .name("New Property")
                .categoryId(categoryId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyService.createProperty(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
        verify(categoryRepository).findById(categoryId);
        verify(propertyRepository, never()).save(any());
    }

    @Test
    void updateProperty_WhenPropertyExists_ShouldUpdateFields() {
        // Given
        PropertyUpdateRequestDTO request = PropertyUpdateRequestDTO.builder()
                .name("Updated Property")
                .unit("g")
                .required(false)
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // When
        PropertyResponseDTO result = propertyService.updateProperty(propertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(propertyId);
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void updateProperty_WhenPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        PropertyUpdateRequestDTO request = PropertyUpdateRequestDTO.builder()
                .name("Updated Property")
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyService.updateProperty(propertyId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository, never()).save(any());
    }

    @Test
    void updateProperty_WithNullFields_ShouldNotUpdateNullFields() {
        // Given
        PropertyUpdateRequestDTO request = PropertyUpdateRequestDTO.builder()
                .name("Updated Property")
                // unit and required are null
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // When
        PropertyResponseDTO result = propertyService.updateProperty(propertyId, request);

        // Then
        assertThat(result.getId()).isEqualTo(propertyId);
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void deleteProperty_WhenPropertyExists_ShouldDeleteProperty() {
        // Given
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        // When
        propertyService.deleteProperty(propertyId);

        // Then
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository).delete(testProperty);
    }

    @Test
    void deleteProperty_WhenPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyService.deleteProperty(propertyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository, never()).delete(any());
    }

    @Test
    void createProperty_WithAllDataTypes_ShouldCreateCorrectly() {
        // Test for each PropertyDataType
        PropertyDataType[] dataTypes = {PropertyDataType.TEXT, PropertyDataType.NUMBER,
                                      PropertyDataType.BOOLEAN, PropertyDataType.DATE};

        for (PropertyDataType dataType : dataTypes) {
            // Given
            PropertyCreateRequestDTO request = PropertyCreateRequestDTO.builder()
                    .name("Property " + dataType)
                    .dataType(dataType)
                    .categoryId(categoryId)
                    .required(true)
                    .build();

            Property savedProperty = TestEntitiesBuilder.propertyBuilder()
                    .id(UUID.randomUUID())
                    .dataType(dataType)
                    .category(testCategory)
                    .build();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
            when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

            // When
            PropertyCreateResponseDTO result = propertyService.createProperty(request);

            // Then
            assertThat(result.getId()).isNotNull();
            verify(propertyRepository, atLeastOnce()).save(any(Property.class));
        }
    }
}