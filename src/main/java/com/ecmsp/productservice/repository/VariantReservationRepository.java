package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.VariantReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VariantReservationRepository extends JpaRepository<VariantReservation, UUID> {
    void deleteVariantReservationByReservationId(UUID reservationId);

    List<VariantReservation> getAllByReservationId(UUID reservationId);



}