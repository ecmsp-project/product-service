package com.ecmsp.productservice.kafka.publisher.cart;

import com.ecmsp.productservice.kafka.publisher.cart.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
        prefix = "product.cart-event-publisher",
        name = "type",
        havingValue = "kafka"
)
class KafkaCartEventPublisherConfiguration {

    @Bean
    public KafkaCartEventPublisher kafkaCartEventPublisher(
            KafkaTemplate<String, KafkaVariantPriceChangedEvent> kafkaVariantPriceChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantStockChangedEvent> kafkaVariantStockChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantDeletedEvent> kafkaVariantDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaProductDeletedEvent> kafkaProductDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantImageUpdatedEvent> kafkaVariantImageUpdatedEventKafkaTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        return new KafkaCartEventPublisher(
                kafkaVariantPriceChangedEventKafkaTemplate,
                kafkaVariantStockChangedEventKafkaTemplate,
                kafkaVariantDeletedEventKafkaTemplate,
                kafkaProductDeletedEventKafkaTemplate,
                kafkaVariantImageUpdatedEventKafkaTemplate,
                kafkaTemplate,
                objectMapper
        );
    }

}
