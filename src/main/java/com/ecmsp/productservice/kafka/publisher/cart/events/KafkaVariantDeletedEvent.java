package com.ecmsp.productservice.kafka.publisher.cart.events;

public record KafkaVariantDeletedEvent(
   String variantId,
   String productId
) implements CartEvent {}
