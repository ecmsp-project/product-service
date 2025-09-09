package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Attribute;
import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.AttributeRequestDTO;
import com.ecmsp.productservice.dto.AttributeResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.AttributeRepository;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;

    public AttributeService(AttributeRepository attributeRepository, CategoryRepository categoryRepository) {
        this.attributeRepository = attributeRepository;
        this.categoryRepository = categoryRepository;
    }

    private AttributeResponseDTO convertToDto(Attribute attribute) {
        return AttributeResponseDTO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .unit(attribute.getUnit())
                .dataType(attribute.getDataType())
                .filterable(attribute.isFilterable())
                .categoryId(attribute.getCategory().getId())
                .attributeValueCount(attribute.getAttributeValues().size())
                .variantAttributeCount(attribute.getVariantAttributes().size())
                .build();

        // Attribute builder has default values on below fields. If we use it every time we create new entities, we won't
        // have to check for nullable - but we've got to stick to it.

        // dtoBuilder.attributeValueCount(attribute.getAttributeValues() != null ? attribute.getAttributeValues().size() : 0);
        // dtoBuilder.variantAttributeCount(attribute.getVariantAttributes() != null ? attribute.getVariantAttributes().size() : 0);

    }

    private Attribute convertToEntity(AttributeRequestDTO attributeRequestDTO) {
        Category category = categoryRepository.findById(attributeRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", attributeRequestDTO.getCategoryId()));

        return Attribute.builder()
                .name(attributeRequestDTO.getName())
                .unit(attributeRequestDTO.getUnit())
                .dataType(attributeRequestDTO.getDataType())
                .filterable(attributeRequestDTO.getFilterable())
                .category(category)
                .build();
    }

    public List<AttributeResponseDTO> getAllAttributes() {
        return attributeRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public AttributeResponseDTO getAttributeById(UUID id) {
        return attributeRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));
    }

    public Optional<Attribute> getAttributeEntityById(UUID id) {
        return attributeRepository.findById(id);
    }

    @Transactional
    public AttributeResponseDTO createAttribute(AttributeRequestDTO attributeRequestDTO) {
        if (attributeRequestDTO.getName() == null || attributeRequestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Attribute name cannot be blank.");
        }
        if (attributeRequestDTO.getDataType() == null) {
            throw new IllegalArgumentException("Data type is required.");
        }
        if (attributeRequestDTO.getFilterable() == null) {
            throw new IllegalArgumentException("Filterable status is required.");
        }
        if (attributeRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required.");
        }
        if (attributeRequestDTO.getName().length() > 255) {
            throw new IllegalArgumentException("Attribute name cannot exceed 255 characters.");
        }
        if (attributeRequestDTO.getUnit() != null && attributeRequestDTO.getUnit().length() > 50) {
            throw new IllegalArgumentException("Unit cannot exceed 50 characters.");
        }

        Attribute attribute = convertToEntity(attributeRequestDTO);
        Attribute savedAttribute = attributeRepository.save(attribute);
        return convertToDto(savedAttribute);
    }

    @Transactional
    public AttributeResponseDTO updateAttribute(UUID id, AttributeRequestDTO requestDTO) {
        Attribute existingAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));

        if (requestDTO.getName() != null) {
            if (requestDTO.getName().isBlank()) {
                throw new IllegalArgumentException("Attribute name cannot be blank.");
            }
            if (requestDTO.getName().length() > 255) {
                throw new IllegalArgumentException("Attribute name cannot exceed 255 characters.");
            }
            existingAttribute.setName(requestDTO.getName());
        }
        if (requestDTO.getUnit() != null) {
            if (requestDTO.getUnit().length() > 50) {
                throw new IllegalArgumentException("Unit cannot exceed 50 characters.");
            }
            existingAttribute.setUnit(requestDTO.getUnit());
        }
        if (requestDTO.getFilterable() != null) {
            existingAttribute.setFilterable(requestDTO.getFilterable());
        }

        if (requestDTO.getCategoryId() != null) {
            UUID newCategoryId = requestDTO.getCategoryId();
            UUID currentCategoryId = existingAttribute.getCategory().getId();
            if (!newCategoryId.equals(currentCategoryId)) {
                Category category = categoryRepository.findById(newCategoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", newCategoryId));
                existingAttribute.setCategory(category);
            }
        }

        Attribute updatedAttribute = attributeRepository.save(existingAttribute);
        return convertToDto(updatedAttribute);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));
        attributeRepository.delete(attribute);
    }
}