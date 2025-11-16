package com.ecmsp.productservice.publisher.kafka.cart.events;

import java.math.BigDecimal;

public record KafkaVariantPriceChangedEvent(
   String variantId,
   BigDecimal oldPrice,
   BigDecimal newPrice
) implements CartEvent {}
