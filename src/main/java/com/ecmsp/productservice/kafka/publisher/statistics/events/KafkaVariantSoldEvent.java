package com.ecmsp.productservice.kafka.publisher.statistics.events;

import java.math.BigDecimal;

public record KafkaVariantSoldEvent(
        String eventId,
        String variantId,
        String productId,
        String productName,
        BigDecimal soldAt,
        Integer quantitySold,
        BigDecimal margin,
        Integer stockRemaining
) implements StatisticsEvent {
}
