package com.ecmsp.productservice.grpc;

import com.ecmsp.product.v1.*;
import com.ecmsp.productservice.service.ProductReservationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ProductReservationGrpcService extends ProductReservationServiceGrpc.ProductReservationServiceImplBase {

    private final ProductReservationService productReservationService;

    @Override
    public void reserveProducts(ReserveProductsRequest request, StreamObserver<ReserveProductsResponse> responseObserver) {
        try {
            log.info("Received reservation request for {} products", request.getItemsList().size());

            List<ProductReservationService.ProductReservationRequest> serviceRequests = request.getItemsList()
                .stream()
                .map(item -> ProductReservationService.ProductReservationRequest.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build())
                .collect(Collectors.toList());

            ProductReservationService.ReservationResult result = productReservationService.reserveProducts(serviceRequests);

            ReserveProductsResponse.Builder responseBuilder = ReserveProductsResponse.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .addAllReservedVariantIds(result.getReservedVariantIds());

            // Add failed reservations
            for (ProductReservationService.FailedReservation failedReservation : result.getFailedReservations()) {
                FailedReservation grpcFailedReservation = FailedReservation.newBuilder()
                    .setProductId(failedReservation.getProductId())
                    .setRequestedQuantity(failedReservation.getRequestedQuantity())
                    .setAvailableQuantity(failedReservation.getAvailableQuantity())
                    .setReason(failedReservation.getReason())
                    .build();

                responseBuilder.addFailedReservations(grpcFailedReservation);
            }

            ReserveProductsResponse response = responseBuilder.build();

            log.info("Sending reservation response: success={}, reserved_variants={}, failed_count={}",
                result.isSuccess(), result.getReservedVariantIds().size(), result.getFailedReservations().size());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error processing reservation request", e);

            ReserveProductsResponse errorResponse = ReserveProductsResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Internal error: " + e.getMessage())
                .build();

            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void releaseReservation(ReleaseReservationRequest request, StreamObserver<ReleaseReservationResponse> responseObserver) {
        try {
            log.info("Received release request for {} variant IDs", request.getVariantIdsList().size());

            ProductReservationService.ReleaseResult result = productReservationService
                .releaseReservation(request.getVariantIdsList());

            ReleaseReservationResponse response = ReleaseReservationResponse.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .build();

            log.info("Sending release response: success={}", result.isSuccess());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error processing release request", e);

            ReleaseReservationResponse errorResponse = ReleaseReservationResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Internal error: " + e.getMessage())
                .build();

            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
}