package com.ecmsp.productservice.kafka.repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Outbox save(Object eventPayload, UUID eventId, String eventType) {
        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            Outbox outboxEvent = Outbox.builder()
                    .eventId(eventId)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .eventType(eventType)
                    .build();

            Outbox saved = outboxRepository.save(outboxEvent);
            log.debug("Saved outbox event: id={}, eventType={}", saved.getEventId(), eventType);
            return saved;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload for eventType: {}", eventType, e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Outbox> getUnprocessedEvents() {
        return outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }

    @Transactional
    public void markAsProcessed(UUID eventId) {
        outboxRepository.markAsProcessed(eventId, LocalDateTime.now());
        log.debug("Marked outbox event as processed: id={}", eventId);
    }


    @Transactional
    public void deleteProcessedEvents(LocalDateTime before) {
        outboxRepository.deleteProcessedEventsBefore(before);
        log.info("Deleted processed outbox events older than {}", before);
    }
}