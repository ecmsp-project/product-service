package com.ecmsp.productservice.kafka.publisher.cart.events;

public sealed interface CartEvent permits
        KafkaVariantPriceChangedEvent,
        KafkaVariantStockChangedEvent,
        KafkaVariantDeletedEvent,
        KafkaProductDeletedEvent,
        KafkaVariantImageUpdatedEvent {

}
