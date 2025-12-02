package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    @Query("SELECT DISTINCT d FROM Delivery d " +
           "JOIN FETCH d.items di " +
           "WHERE di.variant.id = :variantId " +
           "AND d.recordedAt BETWEEN :fromDate AND :toDate")
    List<Delivery> findDeliveriesByVariantIdAndDateRange(
            @Param("variantId") UUID variantId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
