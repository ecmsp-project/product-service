package com.ecmsp.productservice.service;

import com.ecmsp.product.v1.reservation.v1.ReservedVariant;
import com.ecmsp.productservice.domain.ReservationStatus;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantReservation;
import com.ecmsp.productservice.dto.variant_reservation.VariantReservationCreateRequestDTO;
import com.ecmsp.productservice.dto.variant_reservation.VariantsReservationCreateRequestDTO;
import com.ecmsp.productservice.repository.VariantReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VariantReservationService {
    private final VariantReservationRepository variantReservationRepository;
    private final VariantService variantService;

    public VariantReservationService(
            VariantReservationRepository variantReservationRepository,
            VariantService variantService) {
        this.variantReservationRepository = variantReservationRepository;
        this.variantService = variantService;
    }

    private VariantReservation convertToEntity(VariantReservationCreateRequestDTO request) {

        Variant variant = variantService.getVariantEntityById(request.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        return VariantReservation.builder()
                .reservationId(request.getReservationId())
                .variant(variant)
                .reservedQuantity(request.getQuantity())
                .status(ReservationStatus.ACTIVE)
                .build();
    }

    private VariantReservation createVariantReservation(VariantReservationCreateRequestDTO request) {

        variantService.reserveVariant(request.getVariantId(), request.getQuantity());

        VariantReservation variantReservation = convertToEntity(request);
        return variantReservationRepository.save(variantReservation);
    }

    @Transactional
    public void createVariantsReservation(VariantsReservationCreateRequestDTO request) {
        request.getVariants().forEach((variantId, quantity) -> {
                VariantReservationCreateRequestDTO bRequest = VariantReservationCreateRequestDTO.builder()
                        .variantId(variantId)
                        .quantity(quantity)
                        .build();
                createVariantReservation(bRequest);
        });
    }

    @Transactional
    public void deleteVariantsReservation(UUID reservationId) {
        variantReservationRepository.deleteVariantReservationByReservationId(reservationId);
    }

    public List<VariantReservation> getReservation(UUID reservationId) {
        return variantReservationRepository.getAllByReservationId(reservationId);
    }
}