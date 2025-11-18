package com.ecmsp.productservice.kafka.repository;

import com.ecmsp.productservice.kafka.publisher.cart.KafkaCartEventPublisher;
import com.ecmsp.productservice.kafka.publisher.statistics.KafkaStatisticsEventPublisher;
import com.ecmsp.productservice.kafka.publisher.statistics.events.KafkaVariantSoldEvent;
import com.ecmsp.productservice.kafka.publisher.statistics.events.KafkaVariantStockUpdatedEvent;
import com.ecmsp.productservice.kafka.publisher.statistics.events.StatisticsEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Test
    void should_serialize_kafka_variant_sold_event_to_correct_json_format() throws JsonProcessingException {
        // Given - create event with known values matching the expected format
        KafkaVariantSoldEvent event = new KafkaVariantSoldEvent(
                "550e8400-e29b-41d4-a716-446655440001",  // eventId
                "660e8400-e29b-41d4-a716-446655440002",  // variantId
                "770e8400-e29b-41d4-a716-446655440003",  // productId
                "Premium Wireless Headphones",            // productName
                new BigDecimal("199.99"),                 // soldAt
                3,                                        // quantitySold
                new BigDecimal("45.50"),                  // margin
                47                                        // stockRemaining
        );

        // When - serialize to JSON with pretty printing for debugging
        String jsonPretty = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(event);
        String json = objectMapper.writeValueAsString(event);

        // Debug output - print the formatted JSON
        System.out.println("\n=== Serialized KafkaVariantSoldEvent JSON (Pretty) ===");
        System.out.println(jsonPretty);
        System.out.println("\n=== Serialized KafkaVariantSoldEvent JSON (Compact) ===");
        System.out.println(json);
        System.out.println("=======================================================\n");

        // Then - verify the JSON structure
        JsonNode jsonNode = objectMapper.readTree(json);

        // Verify all fields are present and have correct values
        assertThat(jsonNode.get("eventId").asText()).isEqualTo("550e8400-e29b-41d4-a716-446655440001");
        assertThat(jsonNode.get("variantId").asText()).isEqualTo("660e8400-e29b-41d4-a716-446655440002");
        assertThat(jsonNode.get("productId").asText()).isEqualTo("770e8400-e29b-41d4-a716-446655440003");
        assertThat(jsonNode.get("productName").asText()).isEqualTo("Premium Wireless Headphones");
        assertThat(jsonNode.get("soldAt").asDouble()).isEqualTo(199.99);
        assertThat(jsonNode.get("quantitySold").asInt()).isEqualTo(3);
        assertThat(jsonNode.get("margin").asDouble()).isEqualTo(45.50);
        assertThat(jsonNode.get("stockRemaining").asInt()).isEqualTo(47);

        // Verify the JSON contains all required fields
        assertThat(json).contains("\"eventId\"");
        assertThat(json).contains("\"variantId\"");
        assertThat(json).contains("\"productId\"");
        assertThat(json).contains("\"productName\"");
        assertThat(json).contains("\"soldAt\"");
        assertThat(json).contains("\"quantitySold\"");
        assertThat(json).contains("\"margin\"");
        assertThat(json).contains("\"stockRemaining\"");

        // Verify UUIDs are serialized as strings (not objects)
        assertThat(jsonNode.get("eventId").isTextual()).isTrue();
        assertThat(jsonNode.get("variantId").isTextual()).isTrue();
        assertThat(jsonNode.get("productId").isTextual()).isTrue();
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
