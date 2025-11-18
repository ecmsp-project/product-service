package com.ecmsp.productservice.kafka.repository;

import com.ecmsp.productservice.kafka.publisher.cart.KafkaCartEventPublisher;
import com.ecmsp.productservice.kafka.publisher.statistics.KafkaStatisticsEventPublisher;
import com.ecmsp.productservice.kafka.publisher.statistics.events.KafkaVariantSoldEvent;
import com.ecmsp.productservice.kafka.publisher.statistics.events.KafkaVariantStockUpdatedEvent;
import com.ecmsp.productservice.kafka.publisher.statistics.events.StatisticsEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventProcessorTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private KafkaCartEventPublisher cartEventPublisher;

    @Mock
    private KafkaStatisticsEventPublisher statisticsEventPublisher;

    private ObjectMapper objectMapper;
    private OutboxEventProcessor processor;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        processor = new OutboxEventProcessor(
                outboxService,
                cartEventPublisher,
                statisticsEventPublisher,
                objectMapper
        );
    }

    @Test
    void should_deserialize_and_route_kafka_variant_sold_event_to_statistics_publisher() throws JsonProcessingException {
        // Given
        KafkaVariantSoldEvent event = new KafkaVariantSoldEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Product",
                new BigDecimal("99.99"),
                5,
                new BigDecimal("20.00"),
                95
        );

        Outbox outbox = createOutboxWithEvent(event, KafkaVariantSoldEvent.class.getName());
        when(outboxService.getUnprocessedEvents()).thenReturn(List.of(outbox));

        // When
        processor.processOutboxEvents();

        // Then
        ArgumentCaptor<StatisticsEvent> captor = ArgumentCaptor.forClass(StatisticsEvent.class);
        verify(statisticsEventPublisher).publish(captor.capture());
        verify(cartEventPublisher, never()).publish(any());
        verify(outboxService).markAsProcessed(outbox.getEventId());

        StatisticsEvent publishedEvent = captor.getValue();
        assertThat(publishedEvent).isInstanceOf(KafkaVariantSoldEvent.class);

        KafkaVariantSoldEvent soldEvent = (KafkaVariantSoldEvent) publishedEvent;
        assertThat(soldEvent.eventId()).isEqualTo(event.eventId());
        assertThat(soldEvent.variantId()).isEqualTo(event.variantId());
        assertThat(soldEvent.productId()).isEqualTo(event.productId());
        assertThat(soldEvent.productName()).isEqualTo(event.productName());
        assertThat(soldEvent.soldAt()).isEqualTo(event.soldAt());
        assertThat(soldEvent.quantitySold()).isEqualTo(event.quantitySold());
        assertThat(soldEvent.margin()).isEqualTo(event.margin());
        assertThat(soldEvent.stockRemaining()).isEqualTo(event.stockRemaining());
    }

    @Test
    void should_deserialize_and_route_kafka_variant_stock_updated_event_to_statistics_publisher() throws JsonProcessingException {
        // Given
        KafkaVariantStockUpdatedEvent event = new KafkaVariantStockUpdatedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                100,
                LocalDateTime.now().toString()
        );

        Outbox outbox = createOutboxWithEvent(event, KafkaVariantStockUpdatedEvent.class.getName());
        when(outboxService.getUnprocessedEvents()).thenReturn(List.of(outbox));

        // When
        processor.processOutboxEvents();

        // Then
        ArgumentCaptor<StatisticsEvent> captor = ArgumentCaptor.forClass(StatisticsEvent.class);
        verify(statisticsEventPublisher).publish(captor.capture());
        verify(cartEventPublisher, never()).publish(any());
        verify(outboxService).markAsProcessed(outbox.getEventId());

        StatisticsEvent publishedEvent = captor.getValue();
        assertThat(publishedEvent).isInstanceOf(KafkaVariantStockUpdatedEvent.class);

        KafkaVariantStockUpdatedEvent stockEvent = (KafkaVariantStockUpdatedEvent) publishedEvent;
        assertThat(stockEvent.eventId()).isEqualTo(event.eventId());
        assertThat(stockEvent.variantId()).isEqualTo(event.variantId());
        assertThat(stockEvent.deliveredQuantity()).isEqualTo(event.deliveredQuantity());
        assertThat(stockEvent.deliveredAt()).isEqualTo(event.deliveredAt());
    }

    private Outbox createOutboxWithEvent(Object event, String eventType) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        return Outbox.builder()
                .eventId(UUID.randomUUID())
                .payload(payload)
                .eventType(eventType)
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
    }
}
