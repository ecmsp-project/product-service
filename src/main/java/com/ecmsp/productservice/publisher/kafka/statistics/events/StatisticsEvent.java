package com.ecmsp.productservice.publisher.kafka.statistics.events;

public sealed interface StatisticsEvent permits
        KafkaVariantSoldEvent,
        KafkaVariantStockUpdatedEvent
 {

}
