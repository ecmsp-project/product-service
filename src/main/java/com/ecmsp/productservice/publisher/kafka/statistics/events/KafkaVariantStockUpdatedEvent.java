package com.ecmsp.productservice.publisher.kafka.statistics.events;

public record KafkaVariantStockUpdatedEvent(
        String eventId,
        String variantId,
        Integer deliveredQuantity,
        String deliveredAt
) implements StatisticsEvent {
}
