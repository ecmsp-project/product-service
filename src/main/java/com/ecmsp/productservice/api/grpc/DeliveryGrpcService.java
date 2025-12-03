package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.delivery.v1.*;
import com.ecmsp.productservice.dto.delivery.*;
import com.ecmsp.productservice.service.DeliveryService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.logging.Logger;

@GrpcService
public class DeliveryGrpcService extends DeliveryServiceGrpc.DeliveryServiceImplBase {
    private static final Logger logger = Logger.getLogger(DeliveryGrpcService.class.getName());

    private final DeliveryService deliveryService;
    private final DeliveryGrpcMapper deliveryGrpcMapper;

    public DeliveryGrpcService(DeliveryService deliveryService, DeliveryGrpcMapper deliveryGrpcMapper) {
        this.deliveryService = deliveryService;
        this.deliveryGrpcMapper = deliveryGrpcMapper;
    }

    @Override
    public void recordDelivery(RecordDeliveryRequest request, StreamObserver<RecordDeliveryResponse> responseObserver) {
        try {
            logger.info("Received recordDelivery request with " + request.getItemsCount() + " items");

            RecordDeliveryRequestDTO requestDTO = deliveryGrpcMapper.toRecordDeliveryRequestDTO(request);
            RecordDeliveryResponseDTO responseDTO = deliveryService.recordDelivery(requestDTO);
            RecordDeliveryResponse response = deliveryGrpcMapper.toRecordDeliveryResponse(responseDTO);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("Successfully recorded delivery: " + responseDTO.getDeliveryId());
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid argument in recordDelivery: " + e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            logger.severe("Error recording delivery: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listDeliveries(ListDeliveriesRequest request, StreamObserver<ListDeliveriesResponse> responseObserver) {
        try {
            logger.info("Received listDeliveries request for variant: " + request.getVariantId());

            ListDeliveriesRequestDTO requestDTO = deliveryGrpcMapper.toListDeliveriesRequestDTO(request);
            List<DeliveryDTO> deliveries = deliveryService.listDeliveries(requestDTO);
            ListDeliveriesResponse response = deliveryGrpcMapper.toListDeliveriesResponse(deliveries);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("Successfully listed " + deliveries.size() + " deliveries");
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid argument in listDeliveries: " + e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            logger.severe("Error listing deliveries: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}
