package com.ecmsp.productservice.api.rest.mappers;

import com.ecmsp.productservice.domain.DefaultPropertyOption;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.VariantProperty;
import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionResponseDTO;
import com.ecmsp.productservice.dto.rest.property.GetPropertyResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public class PropertyMapper {

    public static DefaultPropertyOptionResponseDTO toDefaultPropertyOptionResponseDTO(DefaultPropertyOption defaultPropertyOption) {
        return DefaultPropertyOptionResponseDTO.builder()
                .id(defaultPropertyOption.getId())
                .propertyId(defaultPropertyOption.getProperty().getId())
                .propertyDataType(defaultPropertyOption.getProperty().getDataType())
                .displayText(defaultPropertyOption.getDisplayText())
                .valueBoolean(defaultPropertyOption.getValueBoolean())
                .valueDate(defaultPropertyOption.getValueDate())
                .valueDecimal(defaultPropertyOption.getValueDecimal())
                .valueText(defaultPropertyOption.getValueText())
                .build();
    }

    public static GetPropertyResponseDTO toGetPropertyResponseDTO(Property property) {
        return GetPropertyResponseDTO.builder()
                .id(property.getId())
                .name(property.getName())
                .categoryId(property.getCategory().getId())
                .role(property.getRole())
                .dataType(property.getDataType().toString())
                .defaultPropertyOptions(property.getDefaultPropertyOptions().stream().map(PropertyMapper::toDefaultPropertyOptionResponseDTO).toList())
                .hasDefaultOptions(property.isHasDefaultOptions())
                .build();
    }

    public static Map<String, List<GetPropertyResponseDTO>> toGetPropertyResponseGrouped(List<GetPropertyResponseDTO> properties) {

        Map<String, List<GetPropertyResponseDTO>> propertiesMap = new HashMap<>();
        List<GetPropertyResponseDTO> requiredProperties = new ArrayList<>();
        List<GetPropertyResponseDTO> infoProperties = new ArrayList<>();
        List<GetPropertyResponseDTO> selectableProperties = new ArrayList<>();

        for (GetPropertyResponseDTO property : properties) {
            switch (property.role()) {
                case REQUIRED -> requiredProperties.add(property);
                case INFO -> infoProperties.add(property);
                case SELECTABLE -> selectableProperties.add(property);
            }
        }

        propertiesMap.put("required", requiredProperties);
        propertiesMap.put("info", infoProperties);
        propertiesMap.put("selectable", selectableProperties);
        return propertiesMap;
    }
}
