package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.reservation.v1.*;
import com.ecmsp.productservice.domain.VariantReservation;
import com.ecmsp.productservice.repository.VariantRepository;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantReservationService;
import com.ecmsp.productservice.service.VariantService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@GrpcService
public class VariantReservationGrpcService extends VariantReservationServiceGrpc.VariantReservationServiceImplBase {
    private static final Logger logger = Logger.getLogger(VariantReservationGrpcService.class.getName());

    private final VariantReservationService variantReservationService;

    private final ProductService productService;

    private final VariantService variantService;
    private final VariantRepository variantRepository;

    public VariantReservationGrpcService(
            VariantReservationService variantReservationService,
            ProductService productService,
            VariantService variantService,
            VariantRepository variantRepository) {
        this.productService = productService;
        this.variantService = variantService;
        this.variantRepository = variantRepository;
        this.variantReservationService = variantReservationService;
    }

    @Override
    public void createVariantsReservation(CreateVariantsReservationRequest request, StreamObserver<CreateVariantsReservationResponse> responseObserver) {
        logger.info("got a create variant reservation request");

        UUID reservationId = UUID.randomUUID();
        List<ReservedVariant> reservedVariants = request.getItemsList();

        variantReservationService.createVariantsReservation(reservedVariants, reservationId);

        CreateVariantsReservationResponse response = CreateVariantsReservationResponse
                .newBuilder()
                .setReservationId(reservationId.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a create variant reservation response");
    }

    @Override
    public void removeVariantsReservation(RemoveVariantsReservationRequest request, StreamObserver<RemoveVariantsReservationResponse> responseObserver) {
        logger.info("got a remove variant reservation request");

        UUID reservationId = UUID.fromString(request.getReservationId());
        variantReservationService.deleteVariantsReservation(reservationId);

        RemoveVariantsReservationResponse response = RemoveVariantsReservationResponse
                .newBuilder()
                .setMessage("reservation removed")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a remove variant reservation response");
    }

    @Override
    public void getVariantsReservation(GetVariantsReservationRequest request, StreamObserver<GetVariantsReservationResponse> responseObserver) {
        logger.info("got a get variant reservation request");

        UUID reservationId = UUID.fromString(request.getReservationId());

        List<VariantReservation> variantReservations = variantReservationService.getReservation(reservationId);

        List<ReservedVariant> reservedVariants = variantReservations.stream()
                .map(item ->
                        ReservedVariant.newBuilder()
                            .setVariantId(item.getVariant().getId().toString())
                            .setQuantity(item.getReservedQuantity())
                            .build()
                )
                .toList();

        GetVariantsReservationResponse response = GetVariantsReservationResponse
                .newBuilder()
                .addAllItems(reservedVariants)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a get variant reservation response");
    }
}