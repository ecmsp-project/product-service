package com.ecmsp.productservice.publisher.kafka.statistics;

import com.ecmsp.productservice.publisher.kafka.statistics.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;


class KafkaStatisticsEventPublisher {

    //TODO: inject in specific service and implement bussiness logic to publish events

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.product.variant-sold}")
    private String variantSoldTopic;

    @Value("${kafka.topic.product.variant-stock-updated}")
    private String variantStockUpdatedTopic;

    public KafkaStatisticsEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(StatisticsEvent event) {
        switch (event) {
            case KafkaVariantSoldEvent variantSoldEvent -> {
                sendEvent(variantSoldTopic, variantSoldEvent.eventId(), variantSoldEvent);
            }
            case KafkaVariantStockUpdatedEvent variantStockUpdatedEvent -> {
                sendEvent(variantStockUpdatedTopic, variantStockUpdatedEvent.eventId(), variantStockUpdatedEvent);
            }
        }
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

}
