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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;

    public AttributeService(AttributeRepository attributeRepository, CategoryRepository categoryRepository) {
        this.attributeRepository = attributeRepository;
        this.categoryRepository = categoryRepository;
    }

    private AttributeResponseDTO convertToDto(Attribute attribute) {
        AttributeResponseDTO.AttributeResponseDTOBuilder dtoBuilder = AttributeResponseDTO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .unit(attribute.getUnit())
                .dataType(attribute.getDataType())
                .filterable(attribute.isFilterable());

        if (attribute.getCategory() != null) {
            dtoBuilder.categoryId(attribute.getCategory().getId())
                    .categoryName(attribute.getCategory().getName());
        }

        dtoBuilder.attributeValueCount(attribute.getAttributeValues() != null ? attribute.getAttributeValues().size() : 0);
        dtoBuilder.variantAttributeCount(attribute.getVariantAttributes() != null ? attribute.getVariantAttributes().size() : 0);

        return dtoBuilder.build();
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
                .collect(Collectors.toList());
    }

    public AttributeResponseDTO getAttributeById(UUID id) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));
        return convertToDto(attribute);
    }

    @Transactional
    public AttributeResponseDTO createAttribute(AttributeRequestDTO attributeRequestDTO) {
        Attribute attribute = convertToEntity(attributeRequestDTO);
        Attribute savedAttribute = attributeRepository.save(attribute);
        return convertToDto(savedAttribute);
    }

    @Transactional
    public AttributeResponseDTO updateAttribute(UUID id, AttributeRequestDTO attributeRequestDTO) {
        Attribute existingAttribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));

        existingAttribute.setName(attributeRequestDTO.getName());
        existingAttribute.setUnit(attributeRequestDTO.getUnit());
        existingAttribute.setDataType(attributeRequestDTO.getDataType());
        existingAttribute.setFilterable(attributeRequestDTO.getFilterable());

        if (!existingAttribute.getCategory().getId().equals(attributeRequestDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findById(attributeRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", attributeRequestDTO.getCategoryId()));
            existingAttribute.setCategory(newCategory);
        }

        Attribute updatedAttribute = attributeRepository.save(existingAttribute);
        return convertToDto(updatedAttribute);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        if (!attributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attribute", id);
        }
        attributeRepository.deleteById(id);
    }
}