package com.ecmsp.productservice.api.kafka;

import com.ecmsp.productservice.domain.ReservationStatus;
import com.ecmsp.productservice.dto.variant_reservation.ReservationUpdateRequestDTO;

import java.util.UUID;

record KafkaOrderStatusUpdatedEvent(
        String orderId,
        String status
) {

    public static ReservationUpdateRequestDTO toReservationUpdate(KafkaOrderStatusUpdatedEvent event){
        return ReservationUpdateRequestDTO.builder()
                .reservationId(UUID.fromString(event.orderId))
                .status(mapToReservationStatus(event.status))
                .build();
    }

    private static ReservationStatus mapToReservationStatus(String status){
        return switch (status) {
            case "PAID" -> ReservationStatus.PAYMENT_COMPLETED;
            case "FAILED" -> ReservationStatus.PAYMENT_FAILED;
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }


}
