package com.ecmsp.productservice.publisher.kafka.statistics.events;

import java.math.BigDecimal;

public record KafkaVariantSoldEvent(
        String eventId,
        String variantId,
        BigDecimal price,
        Integer quantitySold,
        BigDecimal margin,
        Integer stockRemaining,
        String soldAt
) implements StatisticsEvent {
}
