package com.ecmsp.productservice.publisher.kafka.events;

public record KafkaVariantImageUpdatedEvent(
   String variantId,
   String imageUrl
) implements ProductEvent {}
