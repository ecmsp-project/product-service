package com.ecmsp.productservice.service;

import com.ecmsp.product.v1.reservation.v1.CreateVariantsReservationRequest;
import com.ecmsp.product.v1.reservation.v1.ReservedVariant;
import com.ecmsp.productservice.domain.ReservationStatus;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantReservation;
import com.ecmsp.productservice.repository.VariantRepository;
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

    private VariantReservation convertToEntity(ReservedVariant reservedVariant, UUID reservationId) {
        UUID variantId = UUID.fromString(reservedVariant.getVariantId());

        Variant variant = variantService.getVariantEntityById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        return VariantReservation.builder()
                .reservationId(reservationId)
                .variant(variant)
                .reservedQuantity(reservedVariant.getQuantity())
                .status(ReservationStatus.ACTIVE)
                .build();
    }

    @Transactional
    public VariantReservation createVariantReservation(ReservedVariant reservedVariant, UUID reservationId) {
        UUID variantId = UUID.fromString(reservedVariant.getVariantId());

        variantService.reserveVariant(variantId, reservedVariant.getQuantity());

        VariantReservation variantReservation = convertToEntity(reservedVariant, reservationId);
        return variantReservationRepository.save(variantReservation);
    }

    @Transactional
    public void createVariantsReservation(List<ReservedVariant> reservedVariants, UUID reservationId) {
        reservedVariants.forEach(reservedVariant -> createVariantReservation(reservedVariant, reservationId));
    }

    @Transactional
    public void deleteVariantsReservation(UUID reservationId) {
        variantReservationRepository.deleteVariantReservationByReservationId(reservationId);
    }

    public List<VariantReservation> getReservation(UUID reservationId) {
        return variantReservationRepository.getAllByReservationId(reservationId);
    }
}