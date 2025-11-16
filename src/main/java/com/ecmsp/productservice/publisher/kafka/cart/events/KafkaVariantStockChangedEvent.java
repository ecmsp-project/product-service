package com.ecmsp.productservice.publisher.kafka.cart.events;

public record KafkaVariantStockChangedEvent(
   String variantId,
   Integer stockQuantity,
   Boolean isAvailable
) implements CartEvent {}
