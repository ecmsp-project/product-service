package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.DefaultPropertyOption;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.dto.default_property_option.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.DefaultPropertyOptionRepository;
import com.ecmsp.productservice.repository.PropertyRepository;
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
class DefaultPropertyOptionServiceTest {

    @Mock
    private DefaultPropertyOptionRepository defaultPropertyOptionRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private DefaultPropertyOptionService defaultPropertyOptionService;

    private Category testCategory;
    private Property textProperty;
    private Property numberProperty;
    private Property booleanProperty;
    private Property dateProperty;
    private DefaultPropertyOption testOption;
    private UUID categoryId;
    private UUID propertyId;
    private UUID optionId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        optionId = UUID.randomUUID();

        testCategory = TestEntitiesBuilder.categoryBuilder()
                .id(categoryId)
                .build();

        textProperty = TestEntitiesBuilder.propertyBuilder()
                .id(propertyId)
                .category(testCategory)
                .dataType(PropertyDataType.TEXT)
                .build();

        numberProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.NUMBER)
                .build();

        booleanProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.BOOLEAN)
                .build();

        dateProperty = TestEntitiesBuilder.propertyBuilder()
                .id(UUID.randomUUID())
                .category(testCategory)
                .dataType(PropertyDataType.DATE)
                .build();

        testOption = TestEntitiesBuilder.textPropertyOptionBuilder()
                .id(optionId)
                .property(textProperty)
                .build();
    }

    @Test
    void getAllDefaultPropertiesOptions_ShouldReturnListOfDefaultPropertyOptionResponseDTO() {
        // Given
        when(defaultPropertyOptionRepository.findAll()).thenReturn(List.of(testOption));

        // When
        List<DefaultPropertyOptionResponseDTO> result = defaultPropertyOptionService.getAllDefaultPropertiesOptions();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(optionId);
        assertThat(result.get(0).getPropertyId()).isEqualTo(propertyId);
        assertThat(result.get(0).getDisplayText()).isEqualTo(testOption.getDisplayText());
        verify(defaultPropertyOptionRepository).findAll();
    }

    @Test
    void getAllDefaultPropertiesOptionsByPropertyId_ShouldReturnFilteredList() {
        // Given
        when(defaultPropertyOptionRepository.findByPropertyId(propertyId)).thenReturn(List.of(testOption));

        // When
        List<DefaultPropertyOptionResponseDTO> result = defaultPropertyOptionService.getAllDefaultPropertiesOptionsByPropertyId(propertyId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(optionId);
        assertThat(result.get(0).getPropertyId()).isEqualTo(propertyId);
        verify(defaultPropertyOptionRepository).findByPropertyId(propertyId);
    }

    @Test
    void getPropertyOptionById_WhenOptionExists_ShouldReturnDefaultPropertyOptionResponseDTO() {
        // Given
        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.of(testOption));

        // When
        DefaultPropertyOptionResponseDTO result = defaultPropertyOptionService.getPropertyOptionById(optionId);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        assertThat(result.getPropertyId()).isEqualTo(propertyId);
        verify(defaultPropertyOptionRepository).findById(optionId);
    }

    @Test
    void getPropertyOptionById_WhenOptionDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.getPropertyOptionById(optionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PropertyOption");
        verify(defaultPropertyOptionRepository).findById(optionId);
    }

    @Test
    void createPropertyOption_ForTextProperty_ShouldCreateSuccessfully() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(propertyId)
                .displayText("Text Option")
                .valueText("Sample Text")
                .build();

        DefaultPropertyOption savedOption = TestEntitiesBuilder.textPropertyOptionBuilder()
                .id(optionId)
                .property(textProperty)
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(textProperty));
        when(defaultPropertyOptionRepository.save(any(DefaultPropertyOption.class))).thenReturn(savedOption);

        // When
        DefaultPropertyOptionCreateResponseDTO result = defaultPropertyOptionService.createPropertyOption(request);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        verify(propertyRepository).findById(propertyId);
        verify(defaultPropertyOptionRepository).save(any(DefaultPropertyOption.class));
    }

    @Test
    void createPropertyOption_ForNumberProperty_ShouldCreateSuccessfully() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(numberProperty.getId())
                .displayText("Number Option")
                .valueDecimal(new BigDecimal("42.5"))
                .build();

        DefaultPropertyOption savedOption = TestEntitiesBuilder.numberPropertyOptionBuilder()
                .id(optionId)
                .property(numberProperty)
                .build();

        when(propertyRepository.findById(numberProperty.getId())).thenReturn(Optional.of(numberProperty));
        when(defaultPropertyOptionRepository.save(any(DefaultPropertyOption.class))).thenReturn(savedOption);

        // When
        DefaultPropertyOptionCreateResponseDTO result = defaultPropertyOptionService.createPropertyOption(request);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        verify(propertyRepository).findById(numberProperty.getId());
        verify(defaultPropertyOptionRepository).save(any(DefaultPropertyOption.class));
    }

    @Test
    void createPropertyOption_ForBooleanProperty_ShouldCreateSuccessfully() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(booleanProperty.getId())
                .displayText("Boolean Option")
                .valueBoolean(true)
                .build();

        DefaultPropertyOption savedOption = TestEntitiesBuilder.booleanPropertyOptionBuilder()
                .id(optionId)
                .property(booleanProperty)
                .build();

        when(propertyRepository.findById(booleanProperty.getId())).thenReturn(Optional.of(booleanProperty));
        when(defaultPropertyOptionRepository.save(any(DefaultPropertyOption.class))).thenReturn(savedOption);

        // When
        DefaultPropertyOptionCreateResponseDTO result = defaultPropertyOptionService.createPropertyOption(request);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        verify(propertyRepository).findById(booleanProperty.getId());
        verify(defaultPropertyOptionRepository).save(any(DefaultPropertyOption.class));
    }

    @Test
    void createPropertyOption_ForDateProperty_ShouldCreateSuccessfully() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(dateProperty.getId())
                .displayText("Date Option")
                .valueDate(LocalDate.of(2024, 1, 1))
                .build();

        DefaultPropertyOption savedOption = TestEntitiesBuilder.datePropertyOptionBuilder()
                .id(optionId)
                .property(dateProperty)
                .build();

        when(propertyRepository.findById(dateProperty.getId())).thenReturn(Optional.of(dateProperty));
        when(defaultPropertyOptionRepository.save(any(DefaultPropertyOption.class))).thenReturn(savedOption);

        // When
        DefaultPropertyOptionCreateResponseDTO result = defaultPropertyOptionService.createPropertyOption(request);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        verify(propertyRepository).findById(dateProperty.getId());
        verify(defaultPropertyOptionRepository).save(any(DefaultPropertyOption.class));
    }

    @Test
    void createPropertyOption_WhenPropertyDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(propertyId)
                .displayText("Option")
                .valueText("Text")
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.createPropertyOption(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        verify(propertyRepository).findById(propertyId);
        verify(defaultPropertyOptionRepository, never()).save(any());
    }

    @Test
    void createPropertyOption_WithMultipleValuesSet_ShouldThrowIllegalArgumentException() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(propertyId)
                .displayText("Invalid Option")
                .valueText("Text")
                .valueDecimal(new BigDecimal("42"))
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(textProperty));

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.createPropertyOption(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exactly one value field must be filled");
        verify(propertyRepository).findById(propertyId);
        verify(defaultPropertyOptionRepository, never()).save(any());
    }

    @Test
    void createPropertyOption_WithNoValuesSet_ShouldThrowIllegalArgumentException() {
        // Given
        DefaultPropertyOptionCreateRequestDTO request = DefaultPropertyOptionCreateRequestDTO.builder()
                .propertyId(propertyId)
                .displayText("Invalid Option")
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(textProperty));

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.createPropertyOption(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exactly one value field must be filled");
        verify(propertyRepository).findById(propertyId);
        verify(defaultPropertyOptionRepository, never()).save(any());
    }

    @Test
    void updateDefaultPropertyOption_WhenOptionExists_ShouldUpdateFields() {
        // Given
        DefaultPropertyOptionUpdateRequestDTO request = DefaultPropertyOptionUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueText("Updated Text")
                .build();

        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.of(testOption));
        when(defaultPropertyOptionRepository.save(any(DefaultPropertyOption.class))).thenReturn(testOption);

        // When
        DefaultPropertyOptionResponseDTO result = defaultPropertyOptionService.updateDefaultPropertyOption(optionId, request);

        // Then
        assertThat(result.getId()).isEqualTo(optionId);
        verify(defaultPropertyOptionRepository).findById(optionId);
        verify(defaultPropertyOptionRepository).save(testOption);
    }

    @Test
    void updateDefaultPropertyOption_WhenOptionDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        DefaultPropertyOptionUpdateRequestDTO request = DefaultPropertyOptionUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .build();

        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.updateDefaultPropertyOption(optionId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("DefaultPropertyOption");
        verify(defaultPropertyOptionRepository).findById(optionId);
        verify(defaultPropertyOptionRepository, never()).save(any());
    }

    @Test
    void deleteDefaultPropertyOption_WhenOptionExists_ShouldDeleteOption() {
        // Given
        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.of(testOption));

        // When
        defaultPropertyOptionService.deleteDefaultPropertyOption(optionId);

        // Then
        verify(defaultPropertyOptionRepository).findById(optionId);
        verify(defaultPropertyOptionRepository).delete(testOption);
    }

    @Test
    void deleteDefaultPropertyOption_WhenOptionDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.deleteDefaultPropertyOption(optionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("DefaultPropertyOption");
        verify(defaultPropertyOptionRepository).findById(optionId);
        verify(defaultPropertyOptionRepository, never()).delete(any());
    }

    @Test
    void updateDefaultPropertyOption_WithWrongDataTypeValue_ShouldThrowIllegalArgumentException() {
        // Given - trying to update text property with null text value
        DefaultPropertyOptionUpdateRequestDTO request = DefaultPropertyOptionUpdateRequestDTO.builder()
                .displayText("Updated Display Text")
                .valueText(null) // This should cause an error for TEXT property
                .build();

        when(defaultPropertyOptionRepository.findById(optionId)).thenReturn(Optional.of(testOption));

        // When & Then
        assertThatThrownBy(() -> defaultPropertyOptionService.updateDefaultPropertyOption(optionId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value text is required");
        verify(defaultPropertyOptionRepository).findById(optionId);
        verify(defaultPropertyOptionRepository, never()).save(any());
    }
}