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

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Type(io.hypersistence.utils.hibernate.type.json.JsonType.class)
    @Column(name = "additional_properties", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Column(name="description", columnDefinition = "text")
    private String description;

    @Column(name = "margin", precision = 5, scale = 2)
    private BigDecimal margin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VariantProperty> variantProperties  = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Variant{id = %s}", id);
    }
}