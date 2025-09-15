package com.ecmsp.productservice.domain;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "variant_properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VariantProperty {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_option_id")
    private PropertyOption propertyOption;

    @Column(name = "custom_value_text", columnDefinition = "text")
    private String customValueText;

    @Column(name = "custom_value_decimal", precision = 10, scale = 2)
    private BigDecimal customValueDecimal;

    @Column(name = "custom_value_boolean")
    private Boolean customValueBoolean;

    @Column(name = "custom_value_date")
    private LocalDate customValueDate;
}