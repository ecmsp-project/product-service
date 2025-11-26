package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Property {

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
    private PropertyDataType dataType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "has_default_options", nullable = false)
    private boolean hasDefaultOptions;

    @Column(name = "role", nullable = false)
    private PropertyRole role;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<DefaultPropertyOption> defaultPropertyOptions = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VariantProperty> variantProperties = new HashSet<>();

    @Override
    public String toString() {
        return String.format("Property{id = %s, name = %s}", id, name);
    }
}
