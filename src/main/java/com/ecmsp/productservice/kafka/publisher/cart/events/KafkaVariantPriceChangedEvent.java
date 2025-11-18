package com.ecmsp.productservice.kafka.publisher.cart.events;

import java.math.BigDecimal;

public record KafkaVariantPriceChangedEvent(
   String variantId,
   BigDecimal oldPrice,
   BigDecimal newPrice
) implements CartEvent {}
