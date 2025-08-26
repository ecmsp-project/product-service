package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Attribute;
import com.ecmsp.productservice.domain.AttributeValue;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantAttribute;
import com.ecmsp.productservice.dto.VariantAttributeRequestDTO;
import com.ecmsp.productservice.dto.VariantAttributeResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.interfaces.AttributesAssignable;
import com.ecmsp.productservice.repository.AttributeRepository;
import com.ecmsp.productservice.repository.AttributeValueRepository;
import com.ecmsp.productservice.repository.VariantAttributeRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VariantAttributeService {

    private final VariantAttributeRepository variantAttributeRepository;
//    private final AttributeValueRepository attributeValueRepository;

//    private final VariantRepository variantRepository;
//    private final AttributeRepository attributeRepository;

    private final AttributeService attributeService;
    private final VariantService variantService;
    private final AttributeValueService attributeValueService;

    public VariantAttributeService(
            VariantAttributeRepository variantAttributeRepository,
            VariantRepository variantRepository,
            AttributeRepository attributeRepository,
            AttributeValueRepository attributeValueRepository,
            AttributeService attributeService,
            VariantService variantService,
            AttributeValueService attributeValueService) {
        this.variantAttributeRepository = variantAttributeRepository;
//        this.variantRepository = variantRepository;
//        this.attributeRepository = attributeRepository;
//        this.attributeValueRepository = attributeValueRepository;

        this.attributeService = attributeService;
        this.variantService = variantService;
        this.attributeValueService = attributeValueService;
    }

    private AttributeValue getAttributeValueEntity(VariantAttributeRequestDTO requestDTO) {
        Attribute attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));

        if (requestDTO.getAttributeValueId() != null) {
            AttributeValue attributeValue = attributeValueService.getAttributeValueEntityById(requestDTO.getAttributeValueId())
                    .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", requestDTO.getAttributeValueId()));

            if (!attributeValue.getAttribute().getId().equals(attribute.getId())) {
                String message = String.format("Provided AttributeValue '%s' doest not belong to the specified Attribute '%s'\n", requestDTO.getAttributeValueId(), attribute.getId());
                throw new IllegalArgumentException(message);

            }
            return attributeValue;
        }
        return null;
    }

    /*
        VariantAttribute represents a specific property of a variant (e.g., weight = 10kg).
        The value type can differ depending on the attribute: text, decimal, boolean, or date.
        This function validates the value type provided in the request and updates the entity accordingly.
     */
    private VariantAttribute setVariantAttributeValueBasedOnType(
            VariantAttribute entity,
            VariantAttributeRequestDTO requestDTO
    ) {
        entity.setAttributeValue(null);
        entity.setValueText(null);
        entity.setValueDecimal(null);
        entity.setValueBoolean(null);
        entity.setValueDate(null);

        Attribute attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));

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

        return entity;
    }

    private VariantAttributeResponseDTO convertToDto(VariantAttribute variantAttribute) {
        VariantAttributeResponseDTO responseDTO = VariantAttributeResponseDTO.builder().build();

        responseDTO.setId(variantAttribute.getId());
        responseDTO.setVariantId(variantAttribute.getVariant().getId());

        Attribute attribute = variantAttribute.getAttribute();
        responseDTO.setAttributeId(attribute.getId());
        responseDTO.setAttributeName(attribute.getName());
        responseDTO.setAttributeUnit(attribute.getUnit());
        responseDTO.setAttributeDataType(attribute.getDataType());

        if (variantAttribute.getAttributeValue() != null) {
            responseDTO.setAttributeValueId(variantAttribute.getAttributeValue().getId());
        }

        switch (attribute.getDataType()) {
            case TEXT -> {
                responseDTO.setValueText(variantAttribute.getValueText());
            }
            case NUMBER -> {
                responseDTO.setValueDecimal(variantAttribute.getValueDecimal());
            }
            case BOOLEAN -> {
                responseDTO.setValueBoolean(variantAttribute.getValueBoolean());
            }
            case DATE -> {
                responseDTO.setValueDate(variantAttribute.getValueDate());
            }
        };

        return responseDTO;
    }


    private VariantAttribute convertToEntity(VariantAttributeRequestDTO requestDTO) {

        List<Function<VariantAttribute, VariantAttribute>> builder = Arrays.asList(
                (variantAttribute) -> {
                    Variant variant = variantService.getVariantEntityById(requestDTO.getVariantId())
                            .orElseThrow(() -> new ResourceNotFoundException("Variant", requestDTO.getVariantId()));
                    variantAttribute.setVariant(variant);
                    return variantAttribute;
                },
                (variantAttribute) -> {
                    Attribute attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));
                    variantAttribute.setAttribute(attribute);
                    return variantAttribute;
                },
                (variantAttribute) -> {
                    AttributeValue attributeValue = getAttributeValueEntity(requestDTO);
                    variantAttribute.setAttributeValue(attributeValue);
                    return variantAttribute;
                },
                (variantAttribute) -> {
                    return setVariantAttributeValueBasedOnType(variantAttribute, requestDTO);
                }
        );

        VariantAttribute variantAttribute = VariantAttribute.builder().build();

        return builder.stream()
                .reduce(Function.identity(), Function::andThen)
                .apply(variantAttribute);
    }

    public List<VariantAttributeResponseDTO> getAllVariantAttributes() {
        return variantAttributeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VariantAttributeResponseDTO getVariantAttributeById(UUID id) {
        VariantAttribute variantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));
        return convertToDto(variantAttribute);
    }

    @Transactional
    public VariantAttributeResponseDTO createVariantAttribute(VariantAttributeRequestDTO requestDTO) {
        VariantAttribute variantAttribute = convertToEntity(requestDTO);
        VariantAttribute savedVariantAttribute = variantAttributeRepository.save(variantAttribute);
        return convertToDto(savedVariantAttribute);
    }

    @Transactional
    public VariantAttributeResponseDTO updateVariantAttribute(UUID id, VariantAttributeRequestDTO requestDTO) {
        VariantAttribute existingVariantAttribute = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", id));

        Attribute currentAttribute = existingVariantAttribute.getAttribute();
        Variant currentVariant = existingVariantAttribute.getVariant();

        if (!currentVariant.getId().equals(requestDTO.getVariantId())) {
            Variant newVariant = findVariantById(requestDTO.getVariantId());
            existingVariantAttribute.setVariant(newVariant);
        }

        if (!currentAttribute.getId().equals(requestDTO.getAttributeId())) {
            Attribute newAttribute = findAttributeById(requestDTO.getAttributeId());
            existingVariantAttribute.setAttribute(newAttribute);
            currentAttribute = newAttribute;
        }

        boolean isAttributeValueIdProvidedInRequest = requestDTO.getAttributeValueId() != null;
        boolean isDirectValueProvidedInRequest = requestDTO.getValueText() != null ||
                requestDTO.getValueDecimal() != null ||
                requestDTO.getValueBoolean() != null ||
                requestDTO.getValueDate() != null;

        if (!isAttributeValueIdProvidedInRequest && !isDirectValueProvidedInRequest) {
            VariantAttribute updatedVariantAttribute = variantAttributeRepository.save(existingVariantAttribute);
            return convertToDto(updatedVariantAttribute);
        }

        boolean isAttributeValueIdProvided = validateAndDetermineValueSource(requestDTO);
        setValueBasedOnAttributeType(existingVariantAttribute, currentAttribute, requestDTO, isAttributeValueIdProvided);

        VariantAttribute updatedVariantAttribute = variantAttributeRepository.save(existingVariantAttribute);
        return convertToDto(updatedVariantAttribute);
    }

    @Transactional
    public void deleteVariantAttribute(UUID id) {
        // let's make delete idempotent
//        if (!variantAttributeRepository.existsById(id)) {
//            throw new ResourceNotFoundException("VariantAttribute", id);
//        }
        variantAttributeRepository.deleteById(id);
    }
}
