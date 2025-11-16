package com.ecmsp.productservice.publisher.kafka.cart.events;

public record KafkaVariantImageUpdatedEvent(
   String variantId,
   String imageUrl
) implements CartEvent {}
