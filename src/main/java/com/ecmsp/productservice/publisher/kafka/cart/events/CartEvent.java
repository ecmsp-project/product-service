package com.ecmsp.productservice.publisher.kafka.cart.events;

public sealed interface CartEvent permits
        KafkaVariantPriceChangedEvent,
        KafkaVariantStockChangedEvent,
        KafkaVariantDeletedEvent,
        KafkaProductDeletedEvent,
        KafkaVariantImageUpdatedEvent {

}
