package com.ecmsp.productservice.kafka.publisher.cart;

import com.ecmsp.productservice.kafka.publisher.cart.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;


public class KafkaCartEventPublisher {

    private final KafkaTemplate<String, KafkaVariantPriceChangedEvent> kafkaVariantPriceChangedEventKafkaTemplate;
    private final KafkaTemplate<String, KafkaVariantStockChangedEvent> kafkaVariantStockChangedEventKafkaTemplate;
    private final KafkaTemplate<String, KafkaVariantDeletedEvent> kafkaVariantDeletedEventKafkaTemplate;
    private final KafkaTemplate<String, KafkaProductDeletedEvent> kafkaProductDeletedEventKafkaTemplate;
    private final KafkaTemplate<String, KafkaVariantImageUpdatedEvent> kafkaVariantImageUpdatedEventKafkaTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.product.variant-price-changed}")
    private String variantPriceChangedTopic;

    @Value("${kafka.topic.product.variant-stock-changed}")
    private String variantStockChangedTopic;

    @Value("${kafka.topic.product.variant-deleted}")
    private String variantDeletedTopic;

    @Value("${kafka.topic.product.product-deleted}")
    private String productDeletedTopic;

    @Value("${kafka.topic.product.variant-image-updated}")
    private String variantImageUpdatedTopic;


    public KafkaCartEventPublisher(
            KafkaTemplate<String, KafkaVariantPriceChangedEvent> kafkaVariantPriceChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantStockChangedEvent> kafkaVariantStockChangedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantDeletedEvent> kafkaVariantDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaProductDeletedEvent> kafkaProductDeletedEventKafkaTemplate,
            KafkaTemplate<String, KafkaVariantImageUpdatedEvent> kafkaVariantImageUpdatedEventKafkaTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaVariantPriceChangedEventKafkaTemplate = kafkaVariantPriceChangedEventKafkaTemplate;
        this.kafkaVariantStockChangedEventKafkaTemplate = kafkaVariantStockChangedEventKafkaTemplate;
        this.kafkaVariantDeletedEventKafkaTemplate = kafkaVariantDeletedEventKafkaTemplate;
        this.kafkaProductDeletedEventKafkaTemplate = kafkaProductDeletedEventKafkaTemplate;
        this.kafkaVariantImageUpdatedEventKafkaTemplate = kafkaVariantImageUpdatedEventKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }



    public void publish(CartEvent event){
        switch (event){
            case KafkaVariantPriceChangedEvent variantPriceChangedEvent -> {
                sendEvent(variantPriceChangedTopic, variantPriceChangedEvent.variantId(), variantPriceChangedEvent);
            }
            case KafkaVariantStockChangedEvent variantStockChangedEvent -> {
                sendEvent(variantStockChangedTopic, variantStockChangedEvent.variantId(), variantStockChangedEvent);
            }
            case KafkaVariantDeletedEvent variantDeletedEvent -> {
                sendEvent(variantDeletedTopic, variantDeletedEvent.variantId(), variantDeletedEvent);
            }
            case KafkaProductDeletedEvent productDeletedEvent -> {
                sendEvent(productDeletedTopic, productDeletedEvent.productId(), productDeletedEvent);
            }
            case KafkaVariantImageUpdatedEvent variantImageUpdatedEvent -> {
                sendEvent(variantImageUpdatedTopic, variantImageUpdatedEvent.variantId(), variantImageUpdatedEvent);
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
