package com.ecmsp.productservice.api.rest.mappers;

import com.ecmsp.productservice.domain.DefaultPropertyOption;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionResponseDTO;
import com.ecmsp.productservice.dto.rest.property.GetPropertyResponseDTO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

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
                .build();
    }
}
