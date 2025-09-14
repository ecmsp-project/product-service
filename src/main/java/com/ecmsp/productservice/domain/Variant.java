package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Variant {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", nullable = false, length = 10)
    private String sku;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private int reservedQuantity = 0;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Type(io.hypersistence.utils.hibernate.type.json.JsonType.class)
    @Column(name = "additional_attributes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> additionalAttributes = new HashMap<>();

    @Column(name="description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VariantAttribute> variantAttributes  = new HashSet<>();

    public int getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }

    public boolean canReserve(int quantity) {
        return getAvailableQuantity() >= quantity;
    }

    public void reserveQuantity(int quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalArgumentException("Insufficient available quantity. Available: " +
                getAvailableQuantity() + ", requested: " + quantity);
        }
        this.reservedQuantity += quantity;
    }

    public void releaseReservation(int quantity) {
        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("Cannot release more than reserved. Reserved: " +
                this.reservedQuantity + ", requested: " + quantity);
        }
        this.reservedQuantity -= quantity;
    }
}