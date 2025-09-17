package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.property.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final CategoryRepository categoryRepository;

    public PropertyService(PropertyRepository propertyRepository, CategoryRepository categoryRepository) {
        this.propertyRepository = propertyRepository;
        this.categoryRepository = categoryRepository;
    }

    private PropertyResponseDTO convertToDto(Property property) {
        return PropertyResponseDTO.builder()
                .id(property.getId())

                .name(property.getName())
                .unit(property.getUnit())
                .dataType(property.getDataType())
                .categoryId(property.getCategory().getId())

                .required(property.isRequired())
                .hasDefaultOptions(property.isHasDefaultOptions())

                .propertyValueCount(property.getDefaultPropertyOptions().size())
                .variantPropertyCount(property.getVariantProperties().size())
                .build();
    }

    private Property convertToEntity(PropertyCreateRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        return Property.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .dataType(request.getDataType())
                .required(request.getRequired())
                .category(category)
                .build();
    }

    public List<PropertyResponseDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public PropertyResponseDTO getPropertyById(UUID id) {
        return propertyRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("AProperty", id));
    }

    @Transactional
    public PropertyCreateResponseDTO createProperty(PropertyCreateRequestDTO request) {
        Property property = convertToEntity(request);
        Property savedProperty = propertyRepository.save(property);

        return PropertyCreateResponseDTO
                .builder()
                .id(savedProperty.getId())
                .build();
    }

    @Transactional
    public PropertyResponseDTO updateProperty(UUID id, PropertyUpdateRequestDTO request) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", id));

        if (request.getName() != null) {
            existingProperty.setName(request.getName());
        }
        if (request.getUnit() != null) {
            existingProperty.setUnit(request.getUnit());
        }
        if (request.getRequired() != null) {
            existingProperty.setRequired(request.getRequired());
        }

        Property updatedProperty = propertyRepository.save(existingProperty);
        return convertToDto(updatedProperty);
    }

    @Transactional
    public void deleteProperty(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", id));
        propertyRepository.delete(property);
    }
}