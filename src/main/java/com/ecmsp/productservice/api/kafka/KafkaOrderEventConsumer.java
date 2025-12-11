package com.ecmsp.productservice.api.kafka;

import com.ecmsp.productservice.dto.variant_reservation.ReservationUpdateRequestDTO;
import com.ecmsp.productservice.service.VariantReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class KafkaOrderEventConsumer {
    private final VariantReservationService variantReservationService;
    private final ObjectMapper objectMapper;

    KafkaOrderEventConsumer(VariantReservationService variantReservationService, ObjectMapper objectMapper) {
        this.variantReservationService = variantReservationService;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(topics = "${kafka.topic.order-status-updated}")
    public void consume(@Payload String orderStatusUpdatedEventJson) throws JsonProcessingException {

        log.info("Raw message received: [{}]", orderStatusUpdatedEventJson);

        KafkaOrderStatusUpdatedEvent orderStatusUpdatedEvent = objectMapper.readValue(orderStatusUpdatedEventJson, KafkaOrderStatusUpdatedEvent.class);

        log.info("Received order status updated request for order: {}", orderStatusUpdatedEvent.orderId());

        try {
            ReservationUpdateRequestDTO reservationToUpdate = KafkaOrderStatusUpdatedEvent.toReservationUpdate(orderStatusUpdatedEvent);
            log.info("Updating reservation for order: {} with status: {}", reservationToUpdate.getReservationId(), reservationToUpdate.getStatus());
            variantReservationService.updateVariantsReservation(reservationToUpdate);
        } catch (Exception e) {
            log.error("Failed to consume order request for order status update: {}", orderStatusUpdatedEvent.orderId(), e);
        }

    }



}
