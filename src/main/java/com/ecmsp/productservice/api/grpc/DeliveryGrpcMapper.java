package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.delivery.v1.*;
import com.ecmsp.productservice.dto.delivery.*;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
class DeliveryGrpcMapper {

    public RecordDeliveryRequestDTO toRecordDeliveryRequestDTO(RecordDeliveryRequest request) {
        List<DeliveryItemDTO> items = request.getItemsList().stream()
                .map(this::toDeliveryItemDTO)
                .toList();

        return RecordDeliveryRequestDTO.builder()
                .items(items)
                .build();
    }

    public RecordDeliveryResponse toRecordDeliveryResponse(RecordDeliveryResponseDTO dto) {
        return RecordDeliveryResponse.newBuilder()
                .setDeliveryId(dto.getDeliveryId().toString())
                .setRecordedAt(toTimestamp(dto.getRecordedAt()))
                .build();
    }

    public ListDeliveriesRequestDTO toListDeliveriesRequestDTO(ListDeliveriesRequest request) {
        return ListDeliveriesRequestDTO.builder()
                .variantId(UUID.fromString(request.getVariantId()))
                .fromDate(request.hasFromDate() ? toLocalDateTime(request.getFromDate()) : null)
                .toDate(request.hasToDate() ? toLocalDateTime(request.getToDate()) : null)
                .build();
    }

    public ListDeliveriesResponse toListDeliveriesResponse(List<DeliveryDTO> deliveries) {
        List<Delivery> deliveryList = deliveries.stream()
                .map(this::toDeliveryProto)
                .toList();

        return ListDeliveriesResponse.newBuilder()
                .addAllDeliveries(deliveryList)
                .build();
    }

    private Delivery toDeliveryProto(DeliveryDTO dto) {
        List<com.ecmsp.product.v1.delivery.v1.DeliveryItem> items = dto.getItems().stream()
                .map(this::toDeliveryItemProto)
                .toList();

        return Delivery.newBuilder()
                .setDeliveryId(dto.getDeliveryId().toString())
                .addAllItems(items)
                .setRecordedAt(toTimestamp(dto.getRecordedAt()))
                .build();
    }

    private DeliveryItemDTO toDeliveryItemDTO(com.ecmsp.product.v1.delivery.v1.DeliveryItem item) {
        return DeliveryItemDTO.builder()
                .variantId(UUID.fromString(item.getVariantId()))
                .quantity(item.getQuantity())
                .build();
    }

    private com.ecmsp.product.v1.delivery.v1.DeliveryItem toDeliveryItemProto(DeliveryItemDTO dto) {
        return com.ecmsp.product.v1.delivery.v1.DeliveryItem.newBuilder()
                .setVariantId(dto.getVariantId().toString())
                .setQuantity(dto.getQuantity())
                .build();
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
