package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attribute {

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

    @Column(name = "unit")
    private String unit;

    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeDataType dataType;

    @Column(name = "filterable", nullable = false)
    private boolean filterable;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AttributeValue> attributeValues = new HashSet<>();

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VariantAttribute> variantAttributes = new HashSet<>();

}
