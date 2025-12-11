package com.ecmsp.productservice.api.grpc;


import com.ecmsp.product.v1.property.v1.*;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.domain.PropertyRole;
import com.ecmsp.productservice.dto.default_property_option.DefaultPropertyOptionCreateRequestDTO;
import com.ecmsp.productservice.dto.property.PropertyCreateRequestDTO;
import com.ecmsp.productservice.dto.property.PropertyCreateResponseDTO;
import com.ecmsp.productservice.service.DefaultPropertyOptionService;
import com.ecmsp.productservice.service.PropertyOrchestratorService;
import com.ecmsp.productservice.service.PropertyService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;

@GrpcService
public class PropertyGrpcService extends PropertyServiceGrpc.PropertyServiceImplBase {

    private final PropertyService propertyService;
    private final DefaultPropertyOptionService defaultPropertyOptionService;
    private final PropertyOrchestratorService propertyOrchestratorService;

    public PropertyGrpcService(
            PropertyService propertyService,
            DefaultPropertyOptionService defaultPropertyOptionService,
            PropertyOrchestratorService propertyOrchestratorService
    ) {
        this.propertyService = propertyService;
        this.defaultPropertyOptionService = defaultPropertyOptionService;
        this.propertyOrchestratorService = propertyOrchestratorService;
    }

    private PropertyRole propertyMapper(com.ecmsp.product.v1.property.v1.PropertyRole propertyRole) {
        return switch (propertyRole) {
            case PROPERTY_ROLE_REQUIRED -> PropertyRole.REQUIRED;
            case PROPERTY_ROLE_SELECTABLE ->  PropertyRole.SELECTABLE;
            case PROPERTY_ROLE_INFO_UNSPECIFIED, UNRECOGNIZED -> PropertyRole.INFO;
        };
    }
    @Override
    public void createProperty(CreatePropertyRequest request, StreamObserver<CreatePropertyResponse> responseObserver) {
        PropertyRole propertyRole = propertyMapper(request.getRole());
        UUID categoryId = UUID.fromString(request.getCategoryId());

        PropertyCreateRequestDTO propertyCreateRequest = PropertyCreateRequestDTO.builder()
                .name(request.getName())
                .role(propertyRole)
                .categoryId(categoryId)
                .unit(request.getUnit())
                .dataType(PropertyDataType.TEXT)
                .build();

        List<DefaultPropertyOptionCreateRequestDTO> defaultPropertyOptionCreateRequests = request.getDefaultPropertyOptionValuesList().stream()
                .map(p ->
                        DefaultPropertyOptionCreateRequestDTO.builder()
                                .propertyId(null)
                                .valueText(p.getDisplayText())
                                .displayText(p.getDisplayText())
                                .build())
                .toList();

        UUID propertyId = propertyOrchestratorService.createFullProperty(propertyCreateRequest, defaultPropertyOptionCreateRequests);

        CreatePropertyResponse response = CreatePropertyResponse.newBuilder()
                .setId(propertyId.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProperty(DeletePropertyRequest request, StreamObserver<DeletePropertyResponse> responseObserver) {
        UUID propertyId = UUID.fromString(request.getId());
        propertyService.deleteProperty(propertyId);

        DeletePropertyResponse response = DeletePropertyResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
