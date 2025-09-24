package com.ecmsp.productservice.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "variant_reservations")
public class VariantReservation {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @Column(name = "status", columnDefinition = "text")
    private ReservationStatus status;

    @Override
    public String toString() {
        return String.format("VariantReservation{id = %s}", id);
    }
}