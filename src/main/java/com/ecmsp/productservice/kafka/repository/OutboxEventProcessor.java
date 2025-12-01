package com.ecmsp.productservice.kafka.repository;
import com.ecmsp.productservice.kafka.publisher.cart.KafkaCartEventPublisher;
import com.ecmsp.productservice.kafka.publisher.cart.events.CartEvent;
import com.ecmsp.productservice.kafka.publisher.statistics.KafkaStatisticsEventPublisher;
import com.ecmsp.productservice.kafka.publisher.statistics.events.StatisticsEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class OutboxEventProcessor {

    private final OutboxService outboxService;
    private final KafkaCartEventPublisher cartEventPublisher;
    private final KafkaStatisticsEventPublisher statisticsEventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        try {
            List<Outbox> unprocessedEvents = outboxService.getUnprocessedEvents();

            if (unprocessedEvents.isEmpty()) {
                return;
            }

            log.debug("Processing {} unprocessed outbox events", unprocessedEvents.size());

            for (Outbox event : unprocessedEvents) {
                processEvent(event);
            }

        } catch (Exception e) {
            log.error("Error processing outbox events", e);
        }
    }

    private void processEvent(Outbox event) {
        try {
            Class<?> eventClass = Class.forName(event.getEventType());
            Object deserializedEvent = objectMapper.readValue(event.getPayload(), eventClass);

            if (deserializedEvent instanceof CartEvent cartEvent) {
                log.debug("Publishing cart event: id={}, type={}", event.getEventId(), event.getEventType());
                cartEventPublisher.publish(cartEvent);
            } else if (deserializedEvent instanceof StatisticsEvent statisticsEvent) {
                log.debug("Publishing statistics event: id={}, type={}", event.getEventId(), event.getEventType());
                statisticsEventPublisher.publish(statisticsEvent);
            } else {
                throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
            }

            outboxService.markAsProcessed(event.getEventId());

            log.info("Successfully processed outbox event: id={}, eventType={}",
                    event.getEventId(), event.getEventType());

        } catch (ClassNotFoundException e) {
            log.error("Event class not found - will retry: id={}, eventType={}",
                    event.getEventId(), event.getEventType(), e);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize event - will retry: id={}, eventType={}",
                    event.getEventId(), event.getEventType(), e);
        } catch (Exception e) {
            log.error("Failed to process outbox event - will retry: id={}, eventType={}",
                    event.getEventId(), event.getEventType(), e);
        }
    }


}