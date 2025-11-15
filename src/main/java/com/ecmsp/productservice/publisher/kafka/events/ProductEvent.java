package com.ecmsp.productservice.publisher.kafka.events;

public sealed interface ProductEvent permits
        KafkaVariantPriceChangedEvent,
        KafkaVariantStockChangedEvent,
        KafkaVariantDeletedEvent,
        KafkaProductDeletedEvent,
        KafkaVariantImageUpdatedEvent {

}
