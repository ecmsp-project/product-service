package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Attribute;
import com.ecmsp.productservice.domain.AttributeValue;
import com.ecmsp.productservice.dto.AttributeValueRequestDTO;
import com.ecmsp.productservice.dto.AttributeValueResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.AttributeRepository;
import com.ecmsp.productservice.repository.AttributeValueRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributeValueService {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeRepository attributeRepository;

    public AttributeValueService(AttributeValueRepository attributeValueRepository, AttributeRepository attributeRepository) {
        this.attributeValueRepository = attributeValueRepository;
        this.attributeRepository = attributeRepository;
    }

    private AttributeValueResponseDTO convertToDto(AttributeValue attributeValue) {
        AttributeValueResponseDTO.AttributeValueResponseDTOBuilder dtoBuilder = AttributeValueResponseDTO.builder()
                .id(attributeValue.getId())
                .value(attributeValue.getValue());
        if (attributeValue.getAttribute() != null) {
            dtoBuilder.attributeId(attributeValue.getAttribute().getId());
        }
        return dtoBuilder.build();
    }

    private AttributeValue convertToEntity(AttributeValueRequestDTO attributeValueRequestDTO) {
        Attribute attribute = attributeRepository.findById(attributeValueRequestDTO.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", attributeValueRequestDTO.getAttributeId()));

        return AttributeValue.builder()
                .value(attributeValueRequestDTO.getValue())
                .attribute(attribute)
                .build();
    }

    public List<AttributeValueResponseDTO> getAllAttributeValues() {
        return attributeValueRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AttributeValueResponseDTO getAttributeValueById(UUID id) {
        AttributeValue attributeValue = attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));
        return convertToDto(attributeValue);
    }

    @Transactional
    public AttributeValueResponseDTO createAttributeValue(AttributeValueRequestDTO attributeValueRequestDTO) {
        AttributeValue attributeValue = convertToEntity(attributeValueRequestDTO);
        AttributeValue savedAttributeValue = attributeValueRepository.save(attributeValue);
        return convertToDto(savedAttributeValue);
    }

    @Transactional
    public AttributeValueResponseDTO updateAttributeValue(UUID id, AttributeValueRequestDTO attributeValueRequestDTO) {
        AttributeValue existingAttributeValue = attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));

        existingAttributeValue.setValue(attributeValueRequestDTO.getValue());

        if (!existingAttributeValue.getAttribute().getId().equals(attributeValueRequestDTO.getAttributeId())) {
            Attribute newAttribute = attributeRepository.findById(attributeValueRequestDTO.getAttributeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attribute", attributeValueRequestDTO.getAttributeId()));
            existingAttributeValue.setAttribute(newAttribute);
        }

        AttributeValue updatedAttributeValue = attributeValueRepository.save(existingAttributeValue);
        return convertToDto(updatedAttributeValue);
    }

    @Transactional
    public void deleteAttributeValue(UUID id) {
        if (!attributeValueRepository.existsById(id)) {
            throw new ResourceNotFoundException("AttributeValue", id);
        }
        attributeValueRepository.deleteById(id);
    }
}