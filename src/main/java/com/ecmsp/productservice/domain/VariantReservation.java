package com.ecmsp.productservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "variant_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VariantReservation {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "reservation_id")
    private UUID reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "text")
    private ReservationStatus status;

    @Override
    public String toString() {
        return String.format("VariantReservation{id = %s}", id);
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
        expiresAt = new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
    }

}