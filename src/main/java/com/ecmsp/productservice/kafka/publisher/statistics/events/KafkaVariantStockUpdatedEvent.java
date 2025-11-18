package com.ecmsp.productservice.kafka.publisher.statistics.events;

public record KafkaVariantStockUpdatedEvent(
        String eventId,
        String variantId,
        Integer deliveredQuantity,
        String deliveredAt
) implements StatisticsEvent {
}
