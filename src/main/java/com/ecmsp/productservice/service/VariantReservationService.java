package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.ReservationStatus;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantReservation;
import com.ecmsp.productservice.dto.variant_reservation.*;
import com.ecmsp.productservice.repository.VariantReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    protected VariantReservation createVariantReservation(VariantReservationCreateRequestDTO request) {
        boolean reserved = variantService.reserveVariant(request.getVariantId(), request.getQuantity());

        if (!reserved) {
            throw new IllegalStateException("Failed to reserve variant " + request.getVariantId() +
                    " - insufficient stock (race condition detected)");
        }

        VariantReservation variantReservation = convertToEntity(request);
        return variantReservationRepository.save(variantReservation);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public VariantReservationResultDTO createVariantsReservation(VariantsReservationCreateRequestDTO request) {
        List<FailedReservationVariantDTO> failedVariants = new ArrayList<>();

        // Phase 1: Validation - check all variants have sufficient stock
        for (var entry : request.getVariants().entrySet()) {
            UUID variantId = entry.getKey();
            int requestedQuantity = entry.getValue();

            var availableStock = variantService.getAvailableStock(variantId);

            if(availableStock.isEmpty() || availableStock.get() < requestedQuantity){
                failedVariants.add(FailedReservationVariantDTO.builder()
                        .variantId(variantId)
                        .requestedQuantity(requestedQuantity)
                        .availableQuantity(availableStock.orElse(0))
                        .build());

            }

        }

        // If any validation failed, return immediately without reserving
        if (!failedVariants.isEmpty()) {
            return VariantReservationResultDTO.builder()
                    .reservedVariantIds(List.of())
                    .failedVariants(failedVariants)
                    .build();
        }

        // Phase 2: Atomic Reservation - all validations passed
        List<UUID> reservedVariantIds = reserveAllVariantsAtomically(request);

        return VariantReservationResultDTO.builder()
                .reservedVariantIds(reservedVariantIds)
                .failedVariants(List.of())
                .build();
    }

    protected List<UUID> reserveAllVariantsAtomically(VariantsReservationCreateRequestDTO request) {
        List<UUID> reservedIds = new ArrayList<>();

        for (var entry : request.getVariants().entrySet()) {
            UUID variantId = entry.getKey();
            int quantity = entry.getValue();

            VariantReservationCreateRequestDTO variantRequest = VariantReservationCreateRequestDTO.builder()
                    .reservationId(request.getReservationId())
                    .variantId(variantId)
                    .quantity(quantity)
                    .build();

            createVariantReservation(variantRequest);
            reservedIds.add(variantId);
        }

        return reservedIds;
    }

    @Transactional
    public void updateVariantsReservation(ReservationUpdateRequestDTO request){
        UUID reservationId = request.getReservationId();
        List<VariantReservation> reservedVariants = variantReservationRepository.getAllByReservationId(reservationId);

        reservedVariants.forEach(reservedVariant -> {
            reservedVariant.setStatus(request.getStatus());

            if(request.getStatus() == ReservationStatus.PAYMENT_FAILED){
                variantService.releaseReservedVariantStock(
                        reservedVariant.getVariant().getId(),
                        reservedVariant.getReservedQuantity()
                );
            }

            variantReservationRepository.save(reservedVariant);
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