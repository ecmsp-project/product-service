package com.ecmsp.productservice.publisher.kafka;

import com.ecmsp.productservice.publisher.kafka.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
        prefix = "product.event-publisher",
        name = "type",
        havingValue = "kafka"
)
class KafkaProductEventPublisherConfiguration {

    @Bean
    public KafkaProductEventPublisher kafkaProductEventPublisher(
            KafkaTemplate<String, KafkaVariantPriceChangedEvent> variantPriceChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantStockChangedEvent> variantStockChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantDeletedEvent> variantDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaProductDeletedEvent> productDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantImageUpdatedEvent> variantImageUpdatedEventKafkaTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        return new KafkaProductEventPublisher(variantPriceChangedEventKafkaTemplate, variantStockChangedEventKafkaTemplate, variantDeletedEventKafkaTemplate, productDeletedEventKafkaTemplate, variantImageUpdatedEventKafkaTemplate, kafkaTemplate, objectMapper);
    }
}
