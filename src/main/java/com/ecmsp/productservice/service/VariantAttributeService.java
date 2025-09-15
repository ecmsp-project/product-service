package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyOption;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantProperty;
import com.ecmsp.productservice.dto.VariantAttributeRequestDTO;
import com.ecmsp.productservice.dto.VariantAttributeResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.PropertyRepository;
import com.ecmsp.productservice.repository.PropertyOptionRepository;
import com.ecmsp.productservice.repository.VariantPropertyRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VariantAttributeService {

    private final VariantPropertyRepository variantAttributeRepository;
    private final PropertyOptionRepository attributeValueRepository;

    private final VariantRepository variantRepository;
    private final PropertyRepository attributeRepository;

    public VariantAttributeService(
            VariantPropertyRepository variantAttributeRepository,
            VariantRepository variantRepository,
            PropertyRepository attributeRepository,
            PropertyOptionRepository attributeValueRepository) {
        this.variantAttributeRepository = variantAttributeRepository;
        this.variantRepository = variantRepository;
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
    }

    private boolean validateAndDetermineValueSource(VariantAttributeRequestDTO requestDTO) {
        boolean isAttributeValueIdProvided = requestDTO.getAttributeValueId() != null;
        boolean isDirectValueProvided = requestDTO.getValueText() != null ||
                requestDTO.getValueDecimal() != null ||
                requestDTO.getValueBoolean() != null ||
                requestDTO.getValueDate() != null;

        // TODO: As far as I'm concerned, it is possible to have both attributeValueId and direct value field (valueText, valueDecimal, etc.)
        // TODO: AttributeValues table is used to represent values at frontend (as text), but on backend, we would want to keep values of different types
        if (isAttributeValueIdProvided && isDirectValueProvided) {
            throw new IllegalArgumentException("Cannot provide both 'attributeValueId' and direct value fields (valueText, valueDecimal, etc.). Choose one.");
        }
        if (!isAttributeValueIdProvided && !isDirectValueProvided) {
            throw new IllegalArgumentException("Either 'attributeValueId' or a direct value field (valueText, valueDecimal, etc.) must be provided.");
        }
        return isAttributeValueIdProvided;
    }

    private void setValueBasedOnAttributeType(
            VariantProperty entity,
            Property attribute,
            VariantAttributeRequestDTO requestDTO,
            boolean isAttributeValueIdProvided
    ) {
        entity.setAttributeValue(null);
        entity.setValueText(null);
        entity.setValueDecimal(null);
        entity.setValueBoolean(null);
        entity.setValueDate(null);


        if (isAttributeValueIdProvided) {
            PropertyOption attributeValue = attributeValueRepository.findById(requestDTO.getAttributeValueId())
                    .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", requestDTO.getAttributeValueId()));

            if (!attributeValue.getAttribute().getId().equals(attribute.getId())) {
                throw new IllegalArgumentException("Provided AttributeValue '" + requestDTO.getAttributeValueId() +
                        "' does not belong to the specified Attribute '" + attribute.getId() + "'.");
            }
            entity.setAttributeValue(attributeValue);
        } else {
            switch (attribute.getDataType()) {
                case TEXT:
                    if (requestDTO.getValueText() == null) throw new IllegalArgumentException("Text value is required for TEXT data type.");
                    entity.setValueText(requestDTO.getValueText());
                    break;
                case NUMBER:
                    if (requestDTO.getValueDecimal() == null) throw new IllegalArgumentException("Decimal value is required for NUMBER data type.");
                    entity.setValueDecimal(requestDTO.getValueDecimal());
                    break;
                case BOOLEAN:
                    if (requestDTO.getValueBoolean() == null) throw new IllegalArgumentException("Boolean value is required for BOOLEAN data type.");
                    entity.setValueBoolean(requestDTO.getValueBoolean());
                    break;
                case DATE:
                    if (requestDTO.getValueDate() == null) throw new IllegalArgumentException("Date value is required for DATE data type.");
                    entity.setValueDate(requestDTO.getValueDate());
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

    private VariantAttributeResponseDTO convertToDto(VariantProperty variantAttribute) {
        VariantAttributeResponseDTO.VariantAttributeResponseDTOBuilder dtoBuilder = VariantAttributeResponseDTO.builder()
                .id(variantAttribute.getId());

        if (variantAttribute.getVariant() != null) {
            dtoBuilder.variantId(variantAttribute.getVariant().getId());
        }

        Property attribute = variantAttribute.getAttribute();
        // TODO: is putting all that information in DTO response a good choice? Shall we send just attribute id?
        // TODO: On the other hand, in microservices, maybe it is a good approach
        if (attribute != null) {
            dtoBuilder.attributeId(attribute.getId())
                    .attributeName(attribute.getName())
                    .attributeUnit(attribute.getUnit())
                    .attributeDataType(attribute.getDataType());

            String effectiveValue;
            if (variantAttribute.getAttributeValue() != null) {
                dtoBuilder.attributeValueId(variantAttribute.getAttributeValue().getId());
                effectiveValue = variantAttribute.getAttributeValue().getValue();
            } else {
                effectiveValue = switch (attribute.getDataType()) {
                    case TEXT -> {
                        dtoBuilder.valueText(variantAttribute.getValueText());
                        yield variantAttribute.getValueText();
                    }
                    case NUMBER -> {
                        dtoBuilder.valueDecimal(variantAttribute.getValueDecimal());
                        yield variantAttribute.getValueDecimal() != null ? variantAttribute.getValueDecimal().toPlainString() : null;
                    }
                    case BOOLEAN -> {
                        dtoBuilder.valueBoolean(variantAttribute.getValueBoolean());
                        yield variantAttribute.getValueBoolean() != null ? variantAttribute.getValueBoolean().toString() : null;
                    }
                    case DATE -> {
                        dtoBuilder.valueDate(variantAttribute.getValueDate());
                        yield variantAttribute.getValueDate() != null ? variantAttribute.getValueDate().toString() : null;
                    }
                };
            }
            dtoBuilder.effectiveValue(effectiveValue);
        }
        return dtoBuilder.build();
    }

    private VariantProperty convertToEntity(VariantAttributeRequestDTO requestDTO) {
        Variant variant = findVariantById(requestDTO.getVariantId());
        Property attribute = findAttributeById(requestDTO.getAttributeId());

        VariantProperty newVariantAttribute = VariantProperty.builder()
                .variant(variant)
                .attribute(attribute)
                .build();

        boolean isAttributeValueIdProvided = validateAndDetermineValueSource(requestDTO);
        setValueBasedOnAttributeType(newVariantAttribute, attribute, requestDTO, isAttributeValueIdProvided);

        return newVariantAttribute;
    }

    public List<VariantAttributeResponseDTO> getAllVariantAttributes() {
        return variantAttributeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VariantAttributeResponseDTO getVariantAttributeById(UUID id) {
        VariantProperty variantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));
        return convertToDto(variantAttribute);
    }

    @Transactional
    public VariantAttributeResponseDTO createVariantAttribute(VariantAttributeRequestDTO requestDTO) {
        VariantProperty variantAttribute = convertToEntity(requestDTO);
        VariantProperty savedVariantAttribute = variantAttributeRepository.save(variantAttribute);
        return convertToDto(savedVariantAttribute);
    }

    @Transactional
    public VariantAttributeResponseDTO updateVariantAttribute(UUID id, VariantAttributeRequestDTO requestDTO) {
        VariantProperty existingVariantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));

        Property currentAttribute = existingVariantAttribute.getAttribute();
        Variant currentVariant = existingVariantAttribute.getVariant();

        if (!currentVariant.getId().equals(requestDTO.getVariantId())) {
            Variant newVariant = findVariantById(requestDTO.getVariantId());
            existingVariantAttribute.setVariant(newVariant);
        }

        if (!currentAttribute.getId().equals(requestDTO.getAttributeId())) {
            Property newAttribute = findAttributeById(requestDTO.getAttributeId());
            existingVariantAttribute.setAttribute(newAttribute);
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

        boolean isAttributeValueIdProvided = validateAndDetermineValueSource(requestDTO);
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
