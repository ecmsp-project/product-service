package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.AttributeDataType;
import com.ecmsp.productservice.domain.VariantAttribute;
import com.ecmsp.productservice.dto.ProductResponseDTO;
import com.ecmsp.productservice.dto.VariantAttributeRequestDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.VariantAttributeRepository;
import com.ecmsp.productservice.testutil.TestDTOGenerator;
import com.ecmsp.productservice.testutil.TestEntitiesGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@Component
@ExtendWith(MockitoExtension.class)
public class VariantAttributeServiceTest {
    @InjectMocks
    @Autowired
    private VariantAttributeService variantAttributeService;

    @Mock
    private VariantAttributeRepository variantAttributeRepository;

    @Mock
    private AttributeService attributeService;

    @Mock
    private VariantService variantService;

    @Mock
    private AttributeValueService attributeValueService;

    @Test
    public void shouldConvertToEntity() {

        VariantAttributeRequestDTO requestDTO = TestDTOGenerator.randomVariantAttributeRequestDTO();

        when(variantService.getVariantEntityById(requestDTO.getVariantId()))
                .thenReturn(Optional.of(TestEntitiesGenerator.randomVariant()));

        when(attributeService.getAttributeEntityById(requestDTO.getAttributeId()))
                .thenReturn(Optional.of(TestEntitiesGenerator.randomAttribute()));

        VariantAttribute entity = variantAttributeService.convertToEntity(requestDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getVariant()).isNotNull();
        assertThat(entity.getAttribute()).isNotNull();

        assertThat(
                properValueOnVariantAttributeIsSet(entity)
        ).isTrue();

    }

    public static boolean properValueOnVariantAttributeIsSet(VariantAttribute entity) {
        AttributeDataType dataType = entity.getAttribute().getDataType();

        Map<AttributeDataType, Predicate<VariantAttribute>> predicates = Map.of(
                AttributeDataType.TEXT, variantAttribute ->
                        variantAttribute.getValueText() != null &&
                        variantAttribute.getValueDate() == null &&
                        variantAttribute.getValueBoolean() == null &&
                        variantAttribute.getValueDecimal() == null,
                AttributeDataType.BOOLEAN, variantAttribute ->
                        variantAttribute.getValueBoolean() != null &&
                        variantAttribute.getValueDate() == null &&
                        variantAttribute.getValueDecimal() == null &&
                        variantAttribute.getValueText() == null,
                AttributeDataType.DATE, variantAttribute ->
                        variantAttribute.getValueDate() != null &&
                        variantAttribute.getValueDecimal() == null &&
                        variantAttribute.getValueText() == null &&
                        variantAttribute.getValueBoolean() == null,
                AttributeDataType.NUMBER, variantAttribute ->
                        variantAttribute.getValueDecimal() != null &&
                        variantAttribute.getValueDate() == null &&
                        variantAttribute.getValueBoolean() == null &&
                        variantAttribute.getValueText() == null
        );

        return predicates
                .getOrDefault(dataType, variantAttribute -> false)
                .test(entity);
    }
}
