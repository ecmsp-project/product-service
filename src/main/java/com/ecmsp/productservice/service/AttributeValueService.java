package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyOption;
import com.ecmsp.productservice.dto.AttributeValueRequestDTO;
import com.ecmsp.productservice.dto.AttributeValueResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.PropertyOptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributeValueService {

    private final PropertyOptionRepository attributeValueRepository;
    private final PropertyRepository attributeRepository;

    public AttributeValueService(
            PropertyOptionRepository attributeValueRepository,
            PropertyRepository attributeRepository) {
        this.attributeValueRepository = attributeValueRepository;
        this.attributeRepository = attributeRepository;
    }

    private AttributeValueResponseDTO convertToDto(PropertyOption attributeValue) {
        return AttributeValueResponseDTO.builder()
                .id(attributeValue.getId())
                .value(attributeValue.getValue())
                .attributeId(attributeValue.getAttribute().getId())
                .build();
    }

    private PropertyOption convertToEntity(AttributeValueRequestDTO attributeValueRequestDTO) {
        Property attribute = attributeRepository.findById(attributeValueRequestDTO.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", attributeValueRequestDTO.getAttributeId()));

        return PropertyOption.builder()
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
        return attributeValueRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));
    }

    @Transactional
    public AttributeValueResponseDTO createAttributeValue(AttributeValueRequestDTO attributeValueRequestDTO) {
        PropertyOption attributeValue = convertToEntity(attributeValueRequestDTO);
        PropertyOption savedAttributeValue = attributeValueRepository.save(attributeValue);
        return convertToDto(savedAttributeValue);
    }

    @Transactional
    public AttributeValueResponseDTO updateAttributeValue(UUID id, AttributeValueRequestDTO attributeValueRequestDTO) {
        PropertyOption existingAttributeValue = attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));

        existingAttributeValue.setValue(attributeValueRequestDTO.getValue());

        UUID newAttributeId = attributeValueRequestDTO.getAttributeId();
        UUID currentAttributeId = existingAttributeValue.getAttribute().getId();

        if (!newAttributeId.equals(currentAttributeId)) {
            Property newAttribute = attributeRepository.findById(attributeValueRequestDTO.getAttributeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attribute", attributeValueRequestDTO.getAttributeId()));
            existingAttributeValue.setAttribute(newAttribute);
        }

        PropertyOption updatedAttributeValue = attributeValueRepository.save(existingAttributeValue);
        return convertToDto(updatedAttributeValue);
    }

    @Transactional
    public void deleteAttributeValue(UUID id) {
        PropertyOption attributeValue = attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", id));
        attributeValueRepository.delete(attributeValue);
    }
}