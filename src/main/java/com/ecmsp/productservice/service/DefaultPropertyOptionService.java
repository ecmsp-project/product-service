package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.DefaultPropertyOption;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.dto.default_property_option.*;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.DefaultPropertyOptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultPropertyOptionService {

    private final DefaultPropertyOptionRepository defaultPropertyOptionRepository;
    private final PropertyRepository propertyRepository;

    public DefaultPropertyOptionService(
            DefaultPropertyOptionRepository defaultPropertyOptionRepository,
            PropertyRepository propertyRepository) {
        this.defaultPropertyOptionRepository = defaultPropertyOptionRepository;
        this.propertyRepository = propertyRepository;
    }

    private DefaultPropertyOptionResponseDTO convertToDto(DefaultPropertyOption defaultPropertyOption) {
        return DefaultPropertyOptionResponseDTO.builder()
                .id(defaultPropertyOption.getId())
                .displayText(defaultPropertyOption.getDisplayText())
                .propertyId(defaultPropertyOption.getProperty().getId())
                .valueText(defaultPropertyOption.getValueText())
                .valueDecimal(defaultPropertyOption.getValueDecimal())
                .valueBoolean(defaultPropertyOption.getValueBoolean())
                .valueDate(defaultPropertyOption.getValueDate())
                .build();
    }

    public DefaultPropertyOption convertToEntity(DefaultPropertyOptionCreateRequestDTO request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", request.getPropertyId()));

        int filledFields = 0;
        if (request.getValueBoolean() != null) filledFields++;
        if (request.getValueDate() != null) filledFields++;
        if (request.getValueDecimal() != null) filledFields++;
        if (request.getValueText() != null) filledFields++;

        if (filledFields != 1) {
            throw new IllegalArgumentException("Exactly one value field must be filled.");
        }

        return switch (property.getDataType()) {
            case BOOLEAN -> DefaultPropertyOption.builder()
                    .valueBoolean(Objects.requireNonNull(request.getValueBoolean(), "Boolean value cannot be null"))
                    .displayText(request.getDisplayText())
                    .property(property)
                    .build();

            case DATE -> DefaultPropertyOption.builder()
                    .valueDate(Objects.requireNonNull(request.getValueDate(), "Date value cannot be null"))
                    .displayText(request.getDisplayText())
                    .property(property)
                    .build();

            case NUMBER -> DefaultPropertyOption.builder()
                    .valueDecimal(Objects.requireNonNull(request.getValueDecimal(), "Decimal value cannot be null"))
                    .displayText(request.getDisplayText())
                    .property(property)
                    .build();

            case TEXT -> DefaultPropertyOption.builder()
                    .valueText(Objects.requireNonNull(request.getValueText(), "Text value cannot be null"))
                    .displayText(request.getDisplayText())
                    .property(property)
                    .build();
        };
    }

    public List<DefaultPropertyOptionResponseDTO> getAllDefaultPropertiesOptions() {
        return defaultPropertyOptionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DefaultPropertyOptionResponseDTO> getAllDefaultPropertiesOptionsByPropertyId(UUID propertyId) {
        return defaultPropertyOptionRepository
                .findByPropertyId(propertyId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public DefaultPropertyOptionResponseDTO getPropertyOptionById(UUID id) {
        return defaultPropertyOptionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyOption", id));
    }

    @Transactional
    public DefaultPropertyOptionCreateResponseDTO createPropertyOption(DefaultPropertyOptionCreateRequestDTO request) {
        DefaultPropertyOption propertyOption = convertToEntity(request);
        DefaultPropertyOption savedAttributeValue = defaultPropertyOptionRepository.save(propertyOption);

        return DefaultPropertyOptionCreateResponseDTO
                .builder()
                .id(savedAttributeValue.getId())
                .build();
    }

    @Transactional
    public DefaultPropertyOptionResponseDTO updateDefaultPropertyOption(UUID id, DefaultPropertyOptionUpdateRequestDTO request) {
        DefaultPropertyOption existingDefaultPropertyOption = defaultPropertyOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DefaultPropertyOption", id));

        if (request.getDisplayText() != null) {
            existingDefaultPropertyOption.setDisplayText(request.getDisplayText());
        }
        if (request.getPropertyId() != null) {
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property", request.getPropertyId()));
            existingDefaultPropertyOption.setProperty(property);
        }

        Property property = existingDefaultPropertyOption.getProperty();
        switch (property.getDataType()) {
            case TEXT -> {
                if (request.getValueText() == null) {
                    throw new IllegalArgumentException("Value text is required");
                }
                existingDefaultPropertyOption.setValueText(request.getValueText());
            }
            case NUMBER -> {
                if (request.getValueDecimal() == null) {
                    throw new IllegalArgumentException("Value decimal is required");
                }
                existingDefaultPropertyOption.setValueDecimal(request.getValueDecimal());
            }
            case BOOLEAN -> {
                if (request.getValueBoolean() == null) {
                    throw new IllegalArgumentException("Value boolean is required");
                }
                existingDefaultPropertyOption.setValueBoolean(request.getValueBoolean());
            }
            case DATE -> {
                if (request.getValueDate() == null) {
                    throw new IllegalArgumentException("Value date is required");
                }
                existingDefaultPropertyOption.setValueDate(request.getValueDate());
            }
        }

        DefaultPropertyOption updatedAttributeValue = defaultPropertyOptionRepository.save(existingDefaultPropertyOption);
        return convertToDto(updatedAttributeValue);
    }

    @Transactional
    public void deleteDefaultPropertyOption(UUID id) {
        DefaultPropertyOption defaultPropertyOption = defaultPropertyOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DefaultPropertyOption", id));
        defaultPropertyOptionRepository.delete(defaultPropertyOption);
    }
}