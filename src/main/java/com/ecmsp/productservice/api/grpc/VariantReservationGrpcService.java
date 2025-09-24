package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.reservation.v1.*;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.logging.Logger;

@GrpcService
public class VariantReservationGrpcService extends VariantReservationServiceGrpc.VariantReservationServiceImplBase {
    private static final Logger logger = Logger.getLogger(VariantReservationGrpcService.class.getName());

    private final ProductService productService;
    private final VariantService variantService;

    public VariantReservationGrpcService(ProductService productService, VariantService variantService) {
        this.productService = productService;
        this.variantService = variantService;
    }

    @Override
    public void createVariantsReservation(CreateVariantsReservationRequest request, StreamObserver<CreateVariantsReservationResponse> responseObserver) {
        logger.info("got a create variant reservation request");

        List<Variant> items = request.getItemsList();

        // TODO: call a proper service method

        CreateVariantsReservationResponse response = CreateVariantsReservationResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a create variant reservation response");
    }

    @Override
    public void removeVariantsReservation(RemoveVariantsReservationRequest request, StreamObserver<RemoveVariantsReservationResponse> responseObserver) {
        logger.info("got a remove variant reservation request");

        String reservationId = request.getReservationId();

        // TODO: call a proper service method

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

        String reservationId = request.getReservationId();

        // TODO: call a proper service method

        GetVariantsReservationResponse response = GetVariantsReservationResponse
                .newBuilder()
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a get variant reservation response");
    }
}