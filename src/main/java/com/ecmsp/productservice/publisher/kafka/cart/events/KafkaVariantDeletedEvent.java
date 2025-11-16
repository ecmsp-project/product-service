package com.ecmsp.productservice.publisher.kafka.cart.events;

public record KafkaVariantDeletedEvent(
   String variantId,
   String productId
) implements CartEvent {}
