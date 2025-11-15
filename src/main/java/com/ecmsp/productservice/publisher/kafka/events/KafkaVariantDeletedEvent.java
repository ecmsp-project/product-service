package com.ecmsp.productservice.publisher.kafka.events;

public record KafkaVariantDeletedEvent(
   String variantId,
   String productId
) implements ProductEvent {}
