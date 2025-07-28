package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "approximate_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal approximatePrice;

    @Column(name = "delivery_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryPrice;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Type(io.hypersistence.utils.hibernate.type.json.JsonType.class)
    @Column(name = "info", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> info = new HashMap<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Variant> variants = new HashSet<>();
}