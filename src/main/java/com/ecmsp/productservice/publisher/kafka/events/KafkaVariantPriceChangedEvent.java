package com.ecmsp.productservice.publisher.kafka.events;

import java.math.BigDecimal;

public record KafkaVariantPriceChangedEvent(
   String variantId,
   BigDecimal oldPrice,
   BigDecimal newPrice
) implements ProductEvent {}
