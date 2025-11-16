package com.ecmsp.productservice.publisher.kafka.events;

public record KafkaVariantStockChangedEvent(
   String variantId,
   Integer stockQuantity,
   Boolean isAvailable
) implements ProductEvent {}
