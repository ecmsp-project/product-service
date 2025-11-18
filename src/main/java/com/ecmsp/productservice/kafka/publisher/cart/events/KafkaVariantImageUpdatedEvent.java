package com.ecmsp.productservice.kafka.publisher.cart.events;

public record KafkaVariantImageUpdatedEvent(
   String variantId,
   String imageUrl
) implements CartEvent {}
