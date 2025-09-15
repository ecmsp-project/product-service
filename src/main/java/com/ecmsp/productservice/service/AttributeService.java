package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.dto.AttributeRequestDTO;
import com.ecmsp.productservice.dto.AttributeResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AttributeService {

    private final PropertyRepository attributeRepository;
    private final CategoryRepository categoryRepository;

    public AttributeService(PropertyRepository attributeRepository, CategoryRepository categoryRepository) {
        this.attributeRepository = attributeRepository;
        this.categoryRepository = categoryRepository;
    }

    private AttributeResponseDTO convertToDto(Property attribute) {
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

    private Property convertToEntity(AttributeRequestDTO attributeRequestDTO) {
        Category category = categoryRepository.findById(attributeRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", attributeRequestDTO.getCategoryId()));

        return Property.builder()
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

    @Transactional
    public AttributeResponseDTO createAttribute(AttributeRequestDTO attributeRequestDTO) {
        Property attribute = convertToEntity(attributeRequestDTO);
        Property savedAttribute = attributeRepository.save(attribute);
        return convertToDto(savedAttribute);
    }

    @Transactional
    public AttributeResponseDTO updateAttribute(UUID id, AttributeRequestDTO attributeRequestDTO) {
        Property existingAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));

        existingAttribute.setName(attributeRequestDTO.getName());
        existingAttribute.setUnit(attributeRequestDTO.getUnit());
        existingAttribute.setDataType(attributeRequestDTO.getDataType());
        existingAttribute.setFilterable(attributeRequestDTO.getFilterable());

        UUID newCategoryId = attributeRequestDTO.getCategoryId();
        UUID currentCategoryId = existingAttribute.getCategory().getId();

        if (!newCategoryId.equals(currentCategoryId)) {
            Category category = categoryRepository.findById(attributeRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", attributeRequestDTO.getCategoryId()));
            existingAttribute.setCategory(category);
        }

        Property updatedAttribute = attributeRepository.save(existingAttribute);
        return convertToDto(updatedAttribute);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        Property attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));
        attributeRepository.delete(attribute);
    }
}