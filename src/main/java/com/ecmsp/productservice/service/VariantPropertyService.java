package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantProperty;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyUpdateRequestDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.DefaultPropertyOptionRepository;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.VariantPropertyRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VariantPropertyService {

    private final VariantPropertyRepository variantPropertyRepository;

    private final VariantRepository variantRepository;
    private final PropertyRepository propertyRepository;

    public VariantPropertyService(
            VariantPropertyRepository variantPropertyRepository,
            VariantRepository variantRepository,
            PropertyRepository propertyRepository,
            DefaultPropertyOptionRepository propertyOptionRepository) {
        this.variantPropertyRepository = variantPropertyRepository;
        this.variantRepository = variantRepository;
        this.propertyRepository = propertyRepository;
    }

    public interface PropertyOptionRequest {
        Boolean getValueBoolean();
        LocalDate getValueDate();
        BigDecimal getValueDecimal();
        String getValueText();
    }

    private void setValueBasedOnPropertyDataType(
            VariantProperty variantProperty,
            Property property,
            PropertyOptionRequest request
    ) {

        switch (property.getDataType()) {
            case TEXT -> {
                if (request.getValueText() == null) {
                    throw new IllegalArgumentException("Text value is required for TEXT data type.");
                }
                variantProperty.setValueText(request.getValueText());
            }
            case NUMBER -> {
                if (request.getValueDecimal() == null) {
                    throw new IllegalArgumentException("Decimal value is required for NUMBER data type.");
                }
                variantProperty.setValueDecimal(request.getValueDecimal());
            }
            case BOOLEAN -> {
                if (request.getValueBoolean() == null) {
                    throw new IllegalArgumentException("Boolean value is required for BOOLEAN data type.");
                }
                variantProperty.setValueBoolean(request.getValueBoolean());
            }
            case DATE -> {
                if (request.getValueDate() == null) {
                    throw new IllegalArgumentException("Date value is required for DATE data type.");
                }
                variantProperty.setValueDate(request.getValueDate());
            }
        }
    }


    private VariantPropertyResponseDTO convertToDto(VariantProperty variantProperty) {

        VariantPropertyResponseDTO response = VariantPropertyResponseDTO.builder().build();

        response.setId(variantProperty.getId());
        response.setVariantId(variantProperty.getVariant().getId());

        Property property = variantProperty.getProperty();
        response.setPropertyId(property.getId());
        response.setPropertyDataType(property.getDataType());

        switch (property.getDataType()) {
            case TEXT -> {
                response.setValueText(variantProperty.getValueText());
            }
            case NUMBER -> {
                response.setValueDecimal(variantProperty.getValueDecimal());
            }
            case BOOLEAN -> {
                response.setValueBoolean(variantProperty.getValueBoolean());
            }
            case DATE -> {
                response.setValueDate(variantProperty.getValueDate());
            }
        };

        return response;
    }

    private VariantProperty convertToEntity(VariantPropertyCreateRequestDTO request) {
        Variant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant", request.getVariantId()));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", request.getPropertyId()));

        return VariantProperty.builder()
                .variant(variant)
                .property(property)
                .build();
    }

    public List<VariantPropertyResponseDTO> getAllVariantProperties() {
        return variantPropertyRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public VariantPropertyResponseDTO getVariantPropertyById(UUID id) {
        VariantProperty variantProperty = variantPropertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantProperty", id));
        return convertToDto(variantProperty);
    }

    @Transactional
    public VariantPropertyResponseDTO createVariantProperty(VariantPropertyCreateRequestDTO request) {
        VariantProperty variantProperty = convertToEntity(request);
        VariantProperty savedVariantProperty = variantPropertyRepository.save(variantProperty);
        return convertToDto(savedVariantProperty);
    }

    @Transactional
    public VariantPropertyResponseDTO updateVariantProperty(UUID id, VariantPropertyUpdateRequestDTO request) {
        VariantProperty existingVariantProperty = variantPropertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantProperty", id));

        Property currentProperty = existingVariantProperty.getProperty();
        Variant currentVariant = existingVariantProperty.getVariant();

        if (request.getVariantId() != null && !currentVariant.getId().equals(request.getVariantId())) {
            Variant newVariant = findVariantById(request.getVariantId());
            existingVariantProperty.setVariant(newVariant);
        }

        if (request.getPropertyId() != null && !currentProperty.getId().equals(request.getPropertyId())) {
            Property newProperty = findPropertyById(request.getPropertyId());
            existingVariantProperty.setProperty(newProperty);
            currentProperty = newProperty;
        }

        boolean isDirectValueProvidedInRequest = request.getCustomValueText() != null ||
                request.getCustomValueDecimal() != null ||
                request.getCustomValueBoolean() != null ||
                request.getCustomValueDate() != null;

        if (!isPropertyOptionIdProvidedInRequest && !isDirectValueProvidedInRequest) {
            VariantProperty updatedVariantProperty = variantPropertyRepository.save(existingVariantProperty);
            return convertToDto(updatedVariantProperty);
        }

        boolean isPropertyOptionIdProvided = validateMutuallyExclusiveCustomFieldsAndOption(request);
        setValueBasedOnPropertyDataType(existingVariantProperty, currentProperty, request, isPropertyOptionIdProvided);

        VariantProperty updatedVariantProperty = variantPropertyRepository.save(existingVariantProperty);
        return convertToDto(updatedVariantProperty);
    }

    @Transactional
    public void deleteVariantProperty(UUID id) {
        if (!variantPropertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("VariantProperty", id);
        }
        variantPropertyRepository.deleteById(id);
    }
}
