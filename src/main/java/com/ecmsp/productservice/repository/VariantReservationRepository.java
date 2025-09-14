package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.VariantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VariantReservationRepository extends JpaRepository<VariantReservation, UUID> {

    List<VariantReservation> findByReservationIdAndStatus(String reservationId, VariantReservation.ReservationStatus status);

    List<VariantReservation> findByVariantIdAndStatus(UUID variantId, VariantReservation.ReservationStatus status);

    @Query("SELECT vr FROM VariantReservation vr WHERE vr.status = 'ACTIVE' AND vr.expiresAt < :currentTime")
    List<VariantReservation> findExpiredActiveReservations(@Param("currentTime") LocalDateTime currentTime);
}