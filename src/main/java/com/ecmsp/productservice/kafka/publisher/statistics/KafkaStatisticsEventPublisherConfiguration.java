package com.ecmsp.productservice.kafka.publisher.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
        prefix = "product.statistics-event-publisher",
        name = "type",
        havingValue = "kafka"
)
class KafkaStatisticsEventPublisherConfiguration {

    @Bean
    public KafkaStatisticsEventPublisher kafkaStatisticsEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        return new KafkaStatisticsEventPublisher(kafkaTemplate, objectMapper);
    }

}
