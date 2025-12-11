package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    @Query(value = """
            SELECT DISTINCT d.*
            FROM deliveries d
            JOIN delivery_items di ON d.id = di.delivery_id
            WHERE di.variant_id = :variantId
            AND d.recorded_at >= COALESCE(:fromDate, '1900-01-01'::timestamp)
            AND d.recorded_at <= COALESCE(:toDate, '2100-01-01'::timestamp)
            """, nativeQuery = true)
    List<Delivery> findDeliveriesByVariantIdAndDateRange(
            @Param("variantId") UUID variantId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
