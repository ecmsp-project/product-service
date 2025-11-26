package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.*;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateResponseDTO;
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
import java.util.*;

@Service
public class VariantPropertyService {

    private final VariantPropertyRepository variantPropertyRepository;

    private final VariantRepository variantRepository;
    private final PropertyRepository propertyRepository;

    private final DefaultPropertyOptionRepository defaultPropertyOptionRepository;
    public VariantPropertyService(
            VariantPropertyRepository variantPropertyRepository,
            VariantRepository variantRepository,
            PropertyRepository propertyRepository,
            DefaultPropertyOptionRepository defaultPropertyOptionRepository) {
        this.variantPropertyRepository = variantPropertyRepository;
        this.variantRepository = variantRepository;
        this.propertyRepository = propertyRepository;
        this.defaultPropertyOptionRepository = defaultPropertyOptionRepository;
    }

    public interface PropertyOptionRequest {
        Boolean getValueBoolean();
        LocalDate getValueDate();
        BigDecimal getValueDecimal();
        String getValueText();
    }

    private VariantProperty setValueBasedOnPropertyDataType(
            VariantProperty variantPropertyOriginal,
            Property property,
            PropertyOptionRequest request
    ) {
        VariantProperty variantProperty = variantPropertyOriginal.toBuilder().build();

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

        return variantProperty;
    }

    private String getDisplayTextBasedOnPropertyDataType(VariantPropertyUpdateRequestDTO request, Property property) {
        return switch(property.getDataType()) {
            case TEXT -> request.getDisplayText();
            case NUMBER -> request.getValueDecimal().toString();
            case BOOLEAN -> request.getValueBoolean().toString();
            case DATE -> request.getValueDate().toString();
        };
    }

    private VariantPropertyResponseDTO convertToDto(VariantProperty variantProperty) {

        VariantPropertyResponseDTO response = VariantPropertyResponseDTO.builder().build();

        response.setId(variantProperty.getId());
        response.setVariantId(variantProperty.getVariant().getId());

        Property property = variantProperty.getProperty();
        response.setPropertyId(property.getId());
        response.setPropertyDataType(property.getDataType());

//        switch (property.getDataType()) {
//            case TEXT -> {
//                response.setValueText(variantProperty.getValueText());
//            }
//            case NUMBER -> {
//                response.setValueDecimal(variantProperty.getValueDecimal());
//            }
//            case BOOLEAN -> {
//                response.setValueBoolean(variantProperty.getValueBoolean());
//            }
//            case DATE -> {
//                response.setValueDate(variantProperty.getValueDate());
//            }
//        };

        response.setValueBoolean(variantProperty.getValueBoolean());
        response.setValueDate(variantProperty.getValueDate());
        response.setValueDecimal(variantProperty.getValueDecimal());
        response.setValueText(variantProperty.getValueText());

        response.setDisplayText(variantProperty.getDisplayText());
        response.setIsDefaultPropertyOption(variantProperty.getProperty().isHasDefaultOptions());
        response.setPropertyName(variantProperty.getProperty().getName());

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
    public VariantPropertyCreateResponseDTO createVariantProperty(VariantPropertyCreateRequestDTO request) {
        VariantProperty variantProperty = convertToEntity(request);
        VariantProperty savedVariantProperty = variantPropertyRepository.save(variantProperty);

        return VariantPropertyCreateResponseDTO
                .builder()
                .id(savedVariantProperty.getId())
                .build();
    }

    @Transactional
    public VariantPropertyResponseDTO updateVariantProperty(UUID id, VariantPropertyUpdateRequestDTO request) {
        VariantProperty existingVariantProperty = variantPropertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantProperty", id));

        Property property = existingVariantProperty.getProperty();
        Variant variant = existingVariantProperty.getVariant();

        VariantProperty variantProperty;

        if (property.isHasDefaultOptions()) {
//            List<DefaultPropertyOption> options = defaultPropertyOptionRepository.findByPropertyId(property.getId());
//            if (!isValidDefaultOptionProvided(request, property, options)) {
//                throw new IllegalArgumentException("Default option provided for property " + property.getId() + " is not valid.");
//            }
            variantProperty = setValueBasedOnPropertyDataType(existingVariantProperty, property, request);
            variantProperty.setDisplayText(request.getDisplayText());
        } else {
            variantProperty = setValueBasedOnPropertyDataType(existingVariantProperty, property, request);

            if (request.getDisplayText() != null) {
                variantProperty.setDisplayText(request.getDisplayText());
            } else {
                variantProperty.setDisplayText(getDisplayTextBasedOnPropertyDataType(request, property));
            }
        }

        VariantProperty updatedVariantProperty = variantPropertyRepository.save(variantProperty);
        return convertToDto(updatedVariantProperty);
    }

    @Transactional
    public void deleteVariantProperty(UUID id) {
        if (!variantPropertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("VariantProperty", id);
        }
        variantPropertyRepository.deleteById(id);
    }

    private boolean isValidDefaultOptionProvided(
            VariantPropertyUpdateRequestDTO request,
            Property property,
            List<DefaultPropertyOption> options) {

        return switch (property.getDataType()) {
            case TEXT -> options.stream()
                    .anyMatch(option -> Objects.equals(option.getValueText(), request.getValueText()));
            case NUMBER -> options.stream()
                    .anyMatch(option -> Objects.equals(option.getValueDecimal(), request.getValueDecimal()));
            case BOOLEAN -> options.stream()
                    .anyMatch(option -> Objects.equals(option.getValueBoolean(), request.getValueBoolean()));
            case DATE -> options.stream()
                    .anyMatch(option -> Objects.equals(option.getValueDate(), request.getValueDate()));
        };
    }

    public VariantProperty getVariantPropertyByVariantIdAndPropertyId(UUID variantId, UUID propertyId) {
        return variantPropertyRepository.findByVariantIdAndPropertyId(variantId, propertyId);
    }

    public Map<String, List<VariantPropertyResponseDTO>> getVariantPropertiesByVariantIdGrouped(UUID variantId) {
        List<VariantProperty> variantProperties = variantPropertyRepository.findByVariantId(variantId);

        Map<String, List<VariantPropertyResponseDTO>> propertiesMap = new HashMap<>();
        List<VariantPropertyResponseDTO> requiredProperties = new ArrayList<>();
        List<VariantPropertyResponseDTO> infoProperties = new ArrayList<>();
        List<VariantPropertyResponseDTO> selectableProperties = new ArrayList<>();

        for (VariantProperty variantProperty : variantProperties) {
            switch (variantProperty.getProperty().getRole()) {
                case REQUIRED -> requiredProperties.add(convertToDto(variantProperty));
                case INFO -> infoProperties.add(convertToDto(variantProperty));
                case SELECTABLE -> selectableProperties.add(convertToDto(variantProperty));
            }
        }

        propertiesMap.put("required", requiredProperties);
        propertiesMap.put("info", infoProperties);
        propertiesMap.put("selectable", selectableProperties);
        return propertiesMap;
    }

    public List<VariantPropertyResponseDTO> getVariantPropertiesByVariantIdAndPropertyRole(
            UUID variantId,
            PropertyRole propertyRole
    ) {
        return variantPropertyRepository.findByVariantIdAndPropertyRole(variantId, propertyRole)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<VariantPropertyResponseDTO> getVariantPropertiesByVariantIdInAndPropertyRole(
            List<UUID> variantIds,
            PropertyRole propertyRole
    ) {
        return variantPropertyRepository.findByVariantIdInAndPropertyRole(variantIds, propertyRole)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<VariantPropertyResponseDTO> getVariantPropertiesByVariantInAndPropertyRole(
            List<Variant> variants,
            PropertyRole propertyRole
    ) {
        return variantPropertyRepository.findByVariantInAndPropertyRole(variants, propertyRole)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

}
