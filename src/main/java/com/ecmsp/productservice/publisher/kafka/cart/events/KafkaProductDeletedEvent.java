package com.ecmsp.productservice.publisher.kafka.cart.events;

import java.util.List;

public record KafkaProductDeletedEvent(
    String productId, // is provided to be used as a key in Kafka messages
    List<String> variantIds
) implements CartEvent {}
