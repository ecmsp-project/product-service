package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name="name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

//  cascade = CascadeType.ALL, orphanRemoval = true
    @OneToMany(mappedBy = "parentCategory")
    @Builder.Default
    private Set<Category> subCategories = new HashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Property> properties = new HashSet<>();

    @Override
    public String toString() {
        return String.format("Category{id = %s, name = %s}", id, name);
    }
}
