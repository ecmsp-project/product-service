package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyOption;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantProperty;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyUpdateRequestDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.PropertyOptionRepository;
import com.ecmsp.productservice.repository.VariantPropertyRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import com.google.type.Decimal;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VariantPropertyService {

    private final VariantPropertyRepository variantPropertyRepository;
    private final PropertyOptionRepository propertyOptionRepository;

    private final VariantRepository variantRepository;
    private final PropertyRepository propertyRepository;

    public VariantPropertyService(
            VariantPropertyRepository variantPropertyRepository,
            VariantRepository variantRepository,
            PropertyRepository propertyRepository,
            PropertyOptionRepository propertyOptionRepository) {
        this.variantPropertyRepository = variantPropertyRepository;
        this.variantRepository = variantRepository;
        this.propertyRepository = propertyRepository;
        this.propertyOptionRepository = propertyOptionRepository;
    }

    public interface PropertyOptionRequest {
        Boolean getCustomValueBoolean();
        LocalDate getCustomValueDate();
        BigDecimal getCustomValueDecimal();
        String getCustomValueText();
        UUID getPropertyOptionId();
    }

    private boolean validateMutuallyExclusiveCustomFieldsAndOption(PropertyOptionRequest request) {
        boolean isPropertyOptionIdProvided = request.getPropertyOptionId() != null;
        boolean isCustomValueProvided = request.getCustomValueText() != null ||
                request.getCustomValueDecimal() != null ||
                request.getCustomValueBoolean() != null ||
                request.getCustomValueDate() != null;

        if (isPropertyOptionIdProvided && isCustomValueProvided) {
            throw new IllegalArgumentException("Cannot provide both 'propertyOptionId' and custom value fields (customValueText, customValueDecimal, etc.)");
        }
        if (!isPropertyOptionIdProvided && !isCustomValueProvided) {
            throw new IllegalArgumentException("Either 'propertyOptionId' or a custom value field (valueText, valueDecimal, etc.) must be provided.");
        }
        return isPropertyOptionIdProvided;
    }

    private void setValueBasedOnPropertyDataType(
            VariantProperty entity,
            Property attribute,
            VariantPropertyRequestDTO requestDTO,
            boolean isAttributeValueIdProvided
    ) {
        entity.setPropertyOption(null);
        entity.setCustomValueText(null);
        entity.setCustomValueDecimal(null);
        entity.setCustomValueBoolean(null);
        entity.setCustomValueDate(null);

        if (isAttributeValueIdProvided) {
            PropertyOption attributeValue = attributeValueRepository.findById(requestDTO.getAttributeValueId())
                    .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", requestDTO.getAttributeValueId()));

            if (!attributeValue.getProperty().getId().equals(attribute.getId())) {
                throw new IllegalArgumentException("Provided AttributeValue '" + requestDTO.getAttributeValueId() +
                        "' does not belong to the specified Attribute '" + attribute.getId() + "'.");
            }
            entity.setPropertyOption(attributeValue);
        } else {
            switch (attribute.getDataType()) {
                case TEXT:
                    if (requestDTO.getValueText() == null) throw new IllegalArgumentException("Text value is required for TEXT data type.");
                    entity.setCustomValueText(requestDTO.getValueText());
                    break;
                case NUMBER:
                    if (requestDTO.getValueDecimal() == null) throw new IllegalArgumentException("Decimal value is required for NUMBER data type.");
                    entity.setCustomValueDecimal(requestDTO.getValueDecimal());
                    break;
                case BOOLEAN:
                    if (requestDTO.getValueBoolean() == null) throw new IllegalArgumentException("Boolean value is required for BOOLEAN data type.");
                    entity.setCustomValueBoolean(requestDTO.getValueBoolean());
                    break;
                case DATE:
                    if (requestDTO.getValueDate() == null) throw new IllegalArgumentException("Date value is required for DATE data type.");
                    entity.setCustomValueDate(requestDTO.getValueDate());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported attribute data type: " + attribute.getDataType());
            }
        }
    }

    // TODO: move this function to variant service
    private Variant findVariantById(UUID variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", variantId));
    }

    // TODO: move this function to attribute service
    private Property findAttributeById(UUID attributeId) {
        return attributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", attributeId));
    }

    private VariantPropertyResponseDTO convertToDto(VariantProperty variantAttribute) {
        VariantPropertyResponseDTO.VariantAttributeResponseDTOBuilder dtoBuilder = VariantPropertyResponseDTO.builder()
                .id(variantAttribute.getId());

        if (variantAttribute.getVariant() != null) {
            dtoBuilder.variantId(variantAttribute.getVariant().getId());
        }

        Property attribute = variantAttribute.getProperty();
        // TODO: is putting all that information in DTO response a good choice? Shall we send just attribute id?
        // TODO: On the other hand, in microservices, maybe it is a good approach
        if (attribute != null) {
            dtoBuilder.attributeId(attribute.getId())
                    .attributeName(attribute.getName())
                    .attributeUnit(attribute.getUnit())
                    .attributeDataType(attribute.getDataType());

            String effectiveValue;
            if (variantAttribute.getPropertyOption() != null) {
                dtoBuilder.attributeValueId(variantAttribute.getPropertyOption().getId());
                effectiveValue = variantAttribute.getPropertyOption().getDisplayText();
            } else {
                effectiveValue = switch (attribute.getDataType()) {
                    case TEXT -> {
                        dtoBuilder.valueText(variantAttribute.getCustomValueText());
                        yield variantAttribute.getCustomValueText();
                    }
                    case NUMBER -> {
                        dtoBuilder.valueDecimal(variantAttribute.getCustomValueDecimal());
                        yield variantAttribute.getCustomValueDecimal() != null ? variantAttribute.getCustomValueDecimal().toPlainString() : null;
                    }
                    case BOOLEAN -> {
                        dtoBuilder.valueBoolean(variantAttribute.getCustomValueBoolean());
                        yield variantAttribute.getCustomValueBoolean() != null ? variantAttribute.getCustomValueBoolean().toString() : null;
                    }
                    case DATE -> {
                        dtoBuilder.valueDate(variantAttribute.getCustomValueDate());
                        yield variantAttribute.getCustomValueDate() != null ? variantAttribute.getCustomValueDate().toString() : null;
                    }
                };
            }
            dtoBuilder.effectiveValue(effectiveValue);
        }
        return dtoBuilder.build();
    }

    private VariantProperty convertToEntity(VariantPropertyCreateRequestDTO request) {
        Variant variant = findVariantById(request.getVariantId());
        Property property = findAttributeById(request.getVariantId());

        VariantProperty newVariantProperty = VariantProperty.builder()
                .variant(variant)
                .property(property)
                .build();

        boolean isAttributeValueIdProvided = validateMutuallyExclusiveCustomFieldsAndOption(request);
        setValueBasedOnAttributeType(newVariantAttribute, attribute, requestDTO, isAttributeValueIdProvided);

        return newVariantAttribute;
    }

    public List<VariantPropertyResponseDTO> getAllVariantAttributes() {
        return variantAttributeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VariantPropertyResponseDTO getVariantAttributeById(UUID id) {
        VariantProperty variantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));
        return convertToDto(variantAttribute);
    }

    @Transactional
    public VariantPropertyResponseDTO createVariantProperty(VariantPropertyCreateRequestDTO request) {
        VariantProperty variantProperty = convertToEntity(request);
        VariantProperty savedVariantProperty = variantPropertyRepository.save(variantProperty);
        return convertToDto(savedVariantProperty);
    }

    @Transactional
    public VariantPropertyResponseDTO updateVariantAttribute(UUID id, VariantPropertyUpdateRequestDTO request) {
        VariantProperty existingVariantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));

        Property currentAttribute = existingVariantAttribute.getProperty();
        Variant currentVariant = existingVariantAttribute.getVariant();

        if (!currentVariant.getId().equals(requestDTO.getVariantId())) {
            Variant newVariant = findVariantById(requestDTO.getVariantId());
            existingVariantAttribute.setVariant(newVariant);
        }

        if (!currentAttribute.getId().equals(requestDTO.getAttributeId())) {
            Property newAttribute = findAttributeById(requestDTO.getAttributeId());
            existingVariantAttribute.setProperty(newAttribute);
            currentAttribute = newAttribute;
        }

        boolean isAttributeValueIdProvidedInRequest = requestDTO.getAttributeValueId() != null;
        boolean isDirectValueProvidedInRequest = requestDTO.getValueText() != null ||
                requestDTO.getValueDecimal() != null ||
                requestDTO.getValueBoolean() != null ||
                requestDTO.getValueDate() != null;

        if (!isAttributeValueIdProvidedInRequest && !isDirectValueProvidedInRequest) {
            VariantProperty updatedVariantAttribute = variantAttributeRepository.save(existingVariantAttribute);
            return convertToDto(updatedVariantAttribute);
        }
        request.get
        boolean isAttributeValueIdProvided = validateMutuallyExclusiveCustomFieldsAndOption(request);
        setValueBasedOnAttributeType(existingVariantAttribute, currentAttribute, requestDTO, isAttributeValueIdProvided);

        VariantProperty updatedVariantAttribute = variantAttributeRepository.save(existingVariantAttribute);
        return convertToDto(updatedVariantAttribute);
    }

    @Transactional
    public void deleteVariantAttribute(UUID id) {
        if (!variantAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("VariantAttribute", id);
        }
        variantAttributeRepository.deleteById(id);
    }
}
