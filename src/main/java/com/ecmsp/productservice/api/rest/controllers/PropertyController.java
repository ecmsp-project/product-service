package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.api.rest.mappers.PropertyMapper;
import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionResponseDTO;
import com.ecmsp.productservice.dto.property.PropertyResponseDTO;
import com.ecmsp.productservice.dto.rest.property.GetPropertiesResponseDTO;
import com.ecmsp.productservice.dto.rest.property.GetPropertyResponseDTO;
import com.ecmsp.productservice.service.DefaultPropertyOptionService;
import com.ecmsp.productservice.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PropertyController {

    private final PropertyService propertyService;
    private final DefaultPropertyOptionService defaultPropertyOptionService;

    public PropertyController(
            PropertyService propertyService,
            DefaultPropertyOptionService defaultPropertyOptionService
    ) {
        this.propertyService = propertyService;
        this.defaultPropertyOptionService = defaultPropertyOptionService;
    }

    @GetMapping("/properties")
    public ResponseEntity<Map<String, List<GetPropertyResponseDTO>>> getPropertiesOfGivenCategory(
            @RequestParam UUID categoryId
    ) {
        List<Property> properties = propertyService.getPropertiesByCategoryId(categoryId);
        List<GetPropertyResponseDTO> initialResponse = properties.stream()
                .map(PropertyMapper::toGetPropertyResponseDTO)
                .toList();

        Map<String, List<GetPropertyResponseDTO>> response = PropertyMapper.toGetPropertyResponseGrouped(initialResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/properties/default-property-options")
    public ResponseEntity<List<GetPropertyResponseDTO>> getPropertiesWithDefaultOptionsOfGivenCategory(
            @RequestParam UUID categoryId
    ) {
        List<Property> properties = propertyService.getPropertiesWithDefaultPropertyOptionsByCategoryId(categoryId);
        List<GetPropertyResponseDTO> response = properties.stream()
                .map(PropertyMapper::toGetPropertyResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }
}
