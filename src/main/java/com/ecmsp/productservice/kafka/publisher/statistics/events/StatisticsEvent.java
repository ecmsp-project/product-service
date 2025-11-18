package com.ecmsp.productservice.kafka.publisher.statistics.events;

public sealed interface StatisticsEvent permits
        KafkaVariantSoldEvent,
        KafkaVariantStockUpdatedEvent
 {

}
