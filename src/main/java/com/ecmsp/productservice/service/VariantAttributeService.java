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
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VariantAttributeService {

    private final VariantAttributeRepository variantAttributeRepository;

    private final AttributeService attributeService;
    private final VariantService variantService;
    private final AttributeValueService attributeValueService;

    public VariantAttributeService(
            VariantAttributeRepository variantAttributeRepository,
            AttributeService attributeService,
            VariantService variantService,
            AttributeValueService attributeValueService) {
        this.variantAttributeRepository = variantAttributeRepository;
        this.attributeService = attributeService;
        this.variantService = variantService;
        this.attributeValueService = attributeValueService;
    }

    /**
        Returns AttributeValue based on AttributeValueID specified in request
        Validates if the AttributeValue entity's attributeID FK matches the one in the request
     */
    private AttributeValue getAttributeValueEntity(VariantAttributeRequestDTO requestDTO) {
        if (requestDTO.getAttributeValueId() == null) { return null; }

        Attribute attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));

        AttributeValue attributeValue = attributeValueService.getAttributeValueEntityById(requestDTO.getAttributeValueId())
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", requestDTO.getAttributeValueId()));

        if (!attributeValue.getAttribute().getId().equals(attribute.getId())) {
            String message = String.format("Provided AttributeValue '%s' doest not belong to the specified Attribute '%s'\n", requestDTO.getAttributeValueId(), attribute.getId());
            throw new IllegalArgumentException(message);

        }
        return attributeValue;
    }

    /**
        VariantAttribute represents a specific property of a variant (e.g., weight = 10kg).
        The value type can differ depending on the attribute: text, decimal, boolean, or date.
        This function validates the value type provided in the request and updates the entity accordingly.

        It is possible to update only the field that matches datatype in Attribute table.
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

        Attribute attribute = entity.getAttribute();
        if (attribute == null) {
            attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));
        }

        switch (attribute.getDataType()) {
            case TEXT -> {
                if (requestDTO.getValueText() == null)
                    throw new IllegalArgumentException("Text value is required for TEXT data type.");
                entity.setValueText(requestDTO.getValueText());
            }
            case NUMBER -> {
                if (requestDTO.getValueDecimal() == null)
                    throw new IllegalArgumentException("Decimal value is required for NUMBER data type.");
                entity.setValueDecimal(requestDTO.getValueDecimal());
            }
            case BOOLEAN -> {
                if (requestDTO.getValueBoolean() == null)
                    throw new IllegalArgumentException("Boolean value is required for BOOLEAN data type.");
                entity.setValueBoolean(requestDTO.getValueBoolean());
            }
            case DATE -> {
                if (requestDTO.getValueDate() == null)
                    throw new IllegalArgumentException("Date value is required for DATE data type.");
                entity.setValueDate(requestDTO.getValueDate());
            }
            default -> {
                throw new IllegalArgumentException("Unsupported attribute data type: " + attribute.getDataType());
            }
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


    public VariantAttribute convertToEntity(VariantAttributeRequestDTO requestDTO) {

        List<Function<VariantAttribute, VariantAttribute>> builder = Arrays.asList(
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();

                    Variant variant = variantService.getVariantEntityById(requestDTO.getVariantId())
                            .orElseThrow(() -> new ResourceNotFoundException("Variant", requestDTO.getVariantId()));
                    variantAttribute.setVariant(variant);
                    return variantAttribute;
                },
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();

                    Attribute attribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));
                    variantAttribute.setAttribute(attribute);
                    return variantAttribute;
                },
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();

                    AttributeValue attributeValue = getAttributeValueEntity(requestDTO);
                    variantAttribute.setAttributeValue(attributeValue);
                    return variantAttribute;
                },
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();
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

    public Optional<VariantAttribute> getVariantAttributeEntityById(UUID id) {
        return variantAttributeRepository.findById(id);
    }

    @Transactional
    public VariantAttributeResponseDTO createVariantAttribute(VariantAttributeRequestDTO requestDTO) {
        VariantAttribute variantAttribute = convertToEntity(requestDTO);
        VariantAttribute savedVariantAttribute = variantAttributeRepository.save(variantAttribute);
        return convertToDto(savedVariantAttribute);
    }

    @Transactional
    public VariantAttributeResponseDTO updateVariantAttribute(UUID variantAttributeId, VariantAttributeRequestDTO requestDTO) {
        List<Function<VariantAttribute, VariantAttribute>> builder = Arrays.asList(
                // Update Variant entity
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();
                    Variant variant = variantAttribute.getVariant();

                    if (!variant.getId().equals(requestDTO.getVariantId())) {
                        Variant newVariant = variantService.getVariantEntityById(requestDTO.getVariantId())
                                .orElseThrow(() -> new ResourceNotFoundException("Variant", requestDTO.getVariantId()));
                        variantAttribute.setVariant(newVariant);
                    }
                    return variantAttribute;
                },
                // Update Attribute entity
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();
                    Attribute attribute = variantAttribute.getAttribute();

                    if (!attribute.getId().equals(requestDTO.getAttributeId())) {
                        Attribute newAttribute = attributeService.getAttributeEntityById(requestDTO.getAttributeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Attribute", requestDTO.getAttributeId()));
                        variantAttribute.setAttribute(newAttribute);
                    }
                    return variantAttribute;
                },
                // Update Value of VariantAttribute and AttributeValue entity
                (variantAttributeOriginal) -> {
                    VariantAttribute variantAttribute = variantAttributeOriginal.toBuilder().build();

                    boolean isAttributeValueIdProvidedInRequest = requestDTO.getAttributeValueId() != null;
                    boolean isDirectValueProvidedInRequest = (
                            requestDTO.getValueText() != null ||
                            requestDTO.getValueDecimal() != null ||
                            requestDTO.getValueBoolean() != null ||
                            requestDTO.getValueDate() != null
                    );

                    if (isDirectValueProvidedInRequest) {
                        setVariantAttributeValueBasedOnType(variantAttribute, requestDTO);
                    }

                    if (isAttributeValueIdProvidedInRequest) {
                        AttributeValue attributeValue = attributeValueService.getAttributeValueEntityById(requestDTO.getAttributeValueId())
                                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", requestDTO.getAttributeValueId()));
                        variantAttribute.setAttributeValue(attributeValue);
                    }
                    return variantAttribute;
                }
        );

        VariantAttribute variantAttribute = variantAttributeRepository.findById(variantAttributeId)
                .orElseThrow(() -> new ResourceNotFoundException("VariantAttribute", variantAttributeId));

        VariantAttribute updatedVariantAttribute = builder.stream()
                .reduce(Function.identity(), Function::andThen)
                .apply(variantAttribute);

        return convertToDto(variantAttributeRepository.save(updatedVariantAttribute));
    }

    @Transactional
    public void deleteVariantAttribute(UUID id) {
        // let's make delete idempotent and not throw exception if the record does not exist in DB
        variantAttributeRepository.deleteById(id);
    }
}
