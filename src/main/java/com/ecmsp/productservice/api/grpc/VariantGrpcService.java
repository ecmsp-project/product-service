package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.variant.v1.*;
import com.ecmsp.productservice.domain.VariantProperty;
import com.ecmsp.productservice.dto.variant.VariantCreateRequestDTO;
import com.ecmsp.productservice.dto.variant.VariantCreateResponseDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant_image.VariantImagesCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyCreateRequestDTO;
import com.ecmsp.productservice.service.VariantImageService;
import com.ecmsp.productservice.service.VariantOrchestratorService;
import com.ecmsp.productservice.service.VariantPropertyService;
import com.ecmsp.productservice.service.VariantService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@GrpcService
public class VariantGrpcService extends VariantServiceGrpc.VariantServiceImplBase {
    private final VariantService variantService;
    private final VariantImageService variantImageService;
    private final VariantPropertyService variantPropertyService;
    private final VariantOrchestratorService variantOrchestratorService;

    public VariantGrpcService(
            VariantService variantService,
            VariantImageService variantImageService,
            VariantPropertyService variantPropertyService,
            VariantOrchestratorService variantOrchestratorService
    ) {
        this.variantService = variantService;
        this.variantImageService = variantImageService;
        this.variantPropertyService = variantPropertyService;
        this.variantOrchestratorService = variantOrchestratorService;
    }

    @Override
    public void createVariant(CreateVariantRequest request, StreamObserver<CreateVariantResponse> responseObserver) {
        UUID productId = UUID.fromString(request.getProductId());
        BigDecimal price = new BigDecimal(request.getPrice().getValue());

        // TODO: add margin to gRPC schema message

        VariantCreateRequestDTO aRequest = VariantCreateRequestDTO.builder()
                .productId(productId)
                .price(price)
                .description(request.getDescription())
                .stockQuantity(request.getStockQuantity())
                .build();

        VariantImagesCreateRequestDTO bRequest = VariantImagesCreateRequestDTO.builder()
                .variantImages(request.getVariantImagesList())
                .build();

        List<VariantPropertyCreateRequestDTO> cRequests = request.getVariantPropertyValuesList().stream()
                .map(v -> VariantPropertyCreateRequestDTO.builder()
                        .variantId(null)
                        .propertyId(UUID.fromString(v.getPropertyId()))
                        .displayText(v.getDisplayText())
                        .valueText(v.getDisplayText())
                        .build())
                .toList();

        UUID variantId = variantOrchestratorService.createFullVariant(
                aRequest,
                bRequest,
                cRequests
        );

        CreateVariantResponse response = CreateVariantResponse.newBuilder()
                .setId(variantId.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteVariant(DeleteVariantRequest request, StreamObserver<DeleteVariantResponse> responseObserver) {
        UUID variantId = UUID.fromString(request.getId());
        variantService.deleteVariant(variantId);

        DeleteVariantResponse response = DeleteVariantResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
