package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Delivery;
import com.ecmsp.productservice.domain.DeliveryItem;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.dto.delivery.*;
import com.ecmsp.productservice.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DeliveryService {
    private static final Logger logger = Logger.getLogger(DeliveryService.class.getName());

    private final DeliveryRepository deliveryRepository;
    private final VariantService variantService;

    public DeliveryService(DeliveryRepository deliveryRepository, VariantService variantService) {
        this.deliveryRepository = deliveryRepository;
        this.variantService = variantService;
    }

    @Transactional
    public RecordDeliveryResponseDTO recordDelivery(RecordDeliveryRequestDTO request) {
        logger.info("Recording delivery with " + request.getItems().size() + " items");

        Delivery delivery = Delivery.builder()
                .items(new ArrayList<>())
                .build();

        for (DeliveryItemDTO itemDTO : request.getItems()) {
            Variant variant = variantService.getVariantEntityById(itemDTO.getVariantId())
                    .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + itemDTO.getVariantId()));

            DeliveryItem deliveryItem = DeliveryItem.builder()
                    .delivery(delivery)
                    .variant(variant)
                    .quantity(itemDTO.getQuantity())
                    .build();

            delivery.getItems().add(deliveryItem);

            variantService.increaseStock(itemDTO.getVariantId(), itemDTO.getQuantity());
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        logger.info("Delivery recorded successfully: " + savedDelivery.getId());

        return RecordDeliveryResponseDTO.builder()
                .deliveryId(savedDelivery.getId())
                .recordedAt(savedDelivery.getRecordedAt())
                .build();
    }

    public List<DeliveryDTO> listDeliveries(ListDeliveriesRequestDTO request) {
        logger.info("Listing deliveries for variant: " + request.getVariantId() +
                " from " + request.getFromDate() + " to " + request.getToDate());

        List<Delivery> deliveries = deliveryRepository.findDeliveriesByVariantIdAndDateRange(
                request.getVariantId(),
                request.getFromDate() != null ? request.getFromDate() : LocalDateTime.MIN,
                request.getToDate() != null ? request.getToDate() : LocalDateTime.MAX
        );

        return deliveries.stream()
                .map(this::toDeliveryDTO)
                .toList();
    }

    private DeliveryDTO toDeliveryDTO(Delivery delivery) {
        List<DeliveryItemDTO> itemDTOs = delivery.getItems().stream()
                .map(item -> DeliveryItemDTO.builder()
                        .variantId(item.getVariant().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return DeliveryDTO.builder()
                .deliveryId(delivery.getId())
                .items(itemDTOs)
                .recordedAt(delivery.getRecordedAt())
                .build();
    }
}
