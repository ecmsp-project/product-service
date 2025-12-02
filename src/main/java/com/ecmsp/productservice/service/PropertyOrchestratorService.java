package com.ecmsp.productservice.service;

import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionCreateRequestDTO;
import com.ecmsp.productservice.dto.property.PropertyCreateRequestDTO;
import com.ecmsp.productservice.dto.property.PropertyCreateResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyOrchestratorService {

    private final PropertyService propertyService;
    private final DefaultPropertyOptionService defaultPropertyOptionService;


    public PropertyOrchestratorService(
            PropertyService propertyService,
            DefaultPropertyOptionService defaultPropertyOptionService
    ) {
        this.propertyService = propertyService;
        this.defaultPropertyOptionService = defaultPropertyOptionService;
    }

    @Transactional
    public UUID createFullProperty(
            PropertyCreateRequestDTO propertyCreateRequest,
            List<DefaultPropertyOptionCreateRequestDTO> defaultPropertyOptionCreateRequests
    ) {
        PropertyCreateResponseDTO property = propertyService.createProperty(propertyCreateRequest);

        for (DefaultPropertyOptionCreateRequestDTO defaultPropertyOptionCreateRequest : defaultPropertyOptionCreateRequests) {
            defaultPropertyOptionCreateRequest.setPropertyId(property.getId());
            defaultPropertyOptionService.createPropertyOption(defaultPropertyOptionCreateRequest);
        }

        return property.getId();
    }
}
