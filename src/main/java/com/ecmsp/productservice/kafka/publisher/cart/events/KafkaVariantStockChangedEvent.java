package com.ecmsp.productservice.kafka.publisher.cart.events;

public record KafkaVariantStockChangedEvent(
   String variantId,
   Integer stockQuantity,
   Boolean isAvailable
) implements CartEvent {}
