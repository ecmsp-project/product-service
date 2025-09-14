package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantReservation;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import com.ecmsp.productservice.repository.VariantReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReservationService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final VariantReservationRepository variantReservationRepository;

    @Transactional
    public ReservationResult reserveProducts(List<ProductReservationRequest> requests) {
        List<String> reservedVariantIds = new ArrayList<>();
        List<FailedReservation> failedReservations = new ArrayList<>();
        String reservationId = UUID.randomUUID().toString();

        for (ProductReservationRequest request : requests) {
            try {
                Optional<Product> productOpt = productRepository.findByProductId(request.getProductId());

                if (productOpt.isEmpty()) {
                    failedReservations.add(FailedReservation.builder()
                        .productId(request.getProductId())
                        .requestedQuantity(request.getQuantity())
                        .availableQuantity(0)
                        .reason("Product not found")
                        .build());
                    continue;
                }

                List<Variant> availableVariants = variantRepository
                    .findAvailableVariantsByProductProductId(request.getProductId(), 1);

                int remainingQuantity = request.getQuantity();

                for (Variant variant : availableVariants) {
                    if (remainingQuantity <= 0) break;

                    int availableQuantity = variant.getAvailableQuantity();
                    int quantityToReserve = Math.min(remainingQuantity, availableQuantity);

                    if (quantityToReserve > 0) {
                        variant.reserveQuantity(quantityToReserve);
                        variantRepository.save(variant);

                        // Create reservation record
                        VariantReservation reservation = VariantReservation.builder()
                            .variant(variant)
                            .reservedQuantity(quantityToReserve)
                            .reservationId(reservationId)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusHours(1)) // 1 hour expiration
                            .status(VariantReservation.ReservationStatus.ACTIVE)
                            .build();
                        variantReservationRepository.save(reservation);

                        reservedVariantIds.add(variant.getId().toString());
                        remainingQuantity -= quantityToReserve;
                    }
                }

                if (remainingQuantity > 0) {
                    int totalAvailable = availableVariants.stream()
                        .mapToInt(Variant::getAvailableQuantity)
                        .sum();

                    failedReservations.add(FailedReservation.builder()
                        .productId(request.getProductId())
                        .requestedQuantity(request.getQuantity())
                        .availableQuantity(totalAvailable)
                        .reason("Insufficient stock")
                        .build());
                }
            } catch (Exception e) {
                log.error("Failed to reserve product {}: {}", request.getProductId(), e.getMessage());
                failedReservations.add(FailedReservation.builder()
                    .productId(request.getProductId())
                    .requestedQuantity(request.getQuantity())
                    .availableQuantity(0)
                    .reason("Error during reservation: " + e.getMessage())
                    .build());
            }
        }

        return ReservationResult.builder()
            .success(failedReservations.isEmpty())
            .message(failedReservations.isEmpty() ? "All products reserved successfully" :
                "Some products could not be reserved")
            .reservedVariantIds(reservedVariantIds)
            .failedReservations(failedReservations)
            .reservationId(reservationId)
            .build();
    }

    @Transactional
    public ReleaseResult releaseReservation(List<String> variantIds) {
        try {
            for (String variantIdStr : variantIds) {
                UUID variantId = UUID.fromString(variantIdStr);
                Optional<Variant> variantOpt = variantRepository.findById(variantId);

                if (variantOpt.isPresent()) {
                    Variant variant = variantOpt.get();

                    // Find active reservations for this variant
                    List<VariantReservation> reservations = variantReservationRepository
                        .findByVariantIdAndStatus(variantId, VariantReservation.ReservationStatus.ACTIVE);

                    int totalToRelease = reservations.stream()
                        .mapToInt(VariantReservation::getReservedQuantity)
                        .sum();

                    if (totalToRelease > 0) {
                        variant.releaseReservation(totalToRelease);
                        variantRepository.save(variant);

                        // Mark reservations as released
                        reservations.forEach(reservation -> {
                            reservation.setStatus(VariantReservation.ReservationStatus.RELEASED);
                            variantReservationRepository.save(reservation);
                        });
                    }
                }
            }

            return ReleaseResult.builder()
                .success(true)
                .message("Reservations released successfully")
                .build();
        } catch (Exception e) {
            log.error("Failed to release reservations: {}", e.getMessage());
            return ReleaseResult.builder()
                .success(false)
                .message("Error releasing reservations: " + e.getMessage())
                .build();
        }
    }

    @Transactional
    public ReleaseResult releaseReservationByReservationId(String reservationId) {
        try {
            List<VariantReservation> reservations = variantReservationRepository
                .findByReservationIdAndStatus(reservationId, VariantReservation.ReservationStatus.ACTIVE);

            for (VariantReservation reservation : reservations) {
                Variant variant = reservation.getVariant();
                variant.releaseReservation(reservation.getReservedQuantity());
                variantRepository.save(variant);

                reservation.setStatus(VariantReservation.ReservationStatus.RELEASED);
                variantReservationRepository.save(reservation);
            }

            return ReleaseResult.builder()
                .success(true)
                .message("Reservation " + reservationId + " released successfully")
                .build();
        } catch (Exception e) {
            log.error("Failed to release reservation {}: {}", reservationId, e.getMessage());
            return ReleaseResult.builder()
                .success(false)
                .message("Error releasing reservation: " + e.getMessage())
                .build();
        }
    }

    // Helper classes
    @lombok.Data
    @lombok.Builder
    public static class ProductReservationRequest {
        private Integer productId;
        private Integer quantity;
    }

    @lombok.Data
    @lombok.Builder
    public static class ReservationResult {
        private boolean success;
        private String message;
        private List<String> reservedVariantIds;
        private List<FailedReservation> failedReservations;
        private String reservationId;
    }

    @lombok.Data
    @lombok.Builder
    public static class FailedReservation {
        private Integer productId;
        private Integer requestedQuantity;
        private Integer availableQuantity;
        private String reason;
    }

    @lombok.Data
    @lombok.Builder
    public static class ReleaseResult {
        private boolean success;
        private String message;
    }
}