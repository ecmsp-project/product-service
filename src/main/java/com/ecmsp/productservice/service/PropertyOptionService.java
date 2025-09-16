package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.domain.PropertyOption;
import com.ecmsp.productservice.dto.property_option.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.PropertyOptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyOptionService {

    private final PropertyOptionRepository propertyOptionRepository;
    private final PropertyRepository propertyRepository;

    public PropertyOptionService(
            PropertyOptionRepository propertyOptionRepository,
            PropertyRepository propertyRepository) {
        this.propertyOptionRepository = propertyOptionRepository;
        this.propertyRepository = propertyRepository;
    }

    private PropertyValueResponseDTO convertToDto(PropertyOption attributeValue) {
        return PropertyValueResponseDTO.builder()
                .id(attributeValue.getId())
                .value(attributeValue.getDisplayText())
                .attributeId(attributeValue.getProperty().getId())
                .build();
    }

    private PropertyOption convertToEntity(PropertyOptionCreateRequestDTO request) {
        Property attribute = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", request.getPropertyId()));

        return PropertyOption.builder()
                .valueDate(request.getValueDate())
                .valueBoolean(request.getValueBoolean())
                .valueDecimal(request.getValueDecimal())
                .valueText(request.getValueText())
                .displayText(request.getDisplayText())
                .property(attribute)
                .build();
    }

    public List<PropertyValueResponseDTO> getAllPropertiesOptions() {
        return propertyOptionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PropertyValueResponseDTO getPropertyOptionById(UUID id) {
        return propertyOptionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyOption", id));
    }

    @Transactional
    public PropertyOptionCreateResponseDTO createPropertyOption(PropertyOptionCreateRequestDTO request) {
        PropertyOption propertyOption = convertToEntity(request);
        PropertyOption savedAttributeValue = propertyOptionRepository.save(propertyOption);

        return PropertyOptionCreateResponseDTO
                .builder()
                .id(savedAttributeValue.getId())
                .build();
    }

    @Transactional
    public PropertyValueResponseDTO updateAttributeValue(UUID id, PropertyOptionUpdateRequestDTO request) {
        PropertyOption existingPropertyOption = propertyOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));

        if (request.getDisplayText() != null) {
            existingPropertyOption.setDisplayText(request.getDisplayText());
        }

        Property property = existingPropertyOption.getProperty();
        switch (property.getDataType()) {
            case TEXT -> {
                if (request.getValueText() == null) {
                    throw new IllegalArgumentException("Value text is required");
                }
                existingPropertyOption.setValueText(request.getValueText());
            }
            case NUMBER -> {
                if (request.getValueDecimal() == null) {
                    throw new IllegalArgumentException("Value decimal is required");
                }
                existingPropertyOption.setValueDecimal(request.getValueDecimal());
            }
            case BOOLEAN -> {
                if (request.getValueBoolean() == null) {
                    throw new IllegalArgumentException("Value boolean is required");
                }
                existingPropertyOption.setValueBoolean(request.getValueBoolean());
            }
            case DATE -> {
                if (request.getValueDate() == null) {
                    throw new IllegalArgumentException("Value date is required");
                }
                existingPropertyOption.setValueDate(request.getValueDate());
            }
        }

        // TODO: for now, let's not allow to change property_id - only values
//        UUID newAttributeId = request.getPropertyId();
//        UUID currentAttributeId = existingPropertyOption.getProperty().getId();
//
//        if (!newAttributeId.equals(currentAttributeId)) {
//            Property newAttribute = propertyRepository.findById(request.getPropertyId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Attribute", request.getAttributeId()));
//            existingPropertyOption.setProperty(newAttribute);
//        }

        PropertyOption updatedAttributeValue = propertyOptionRepository.save(existingPropertyOption);
        return convertToDto(updatedAttributeValue);
    }

    @Transactional
    public void deleteAttributeValue(UUID id) {
        PropertyOption attributeValue = propertyOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));
        propertyOptionRepository.delete(attributeValue);
    }
}