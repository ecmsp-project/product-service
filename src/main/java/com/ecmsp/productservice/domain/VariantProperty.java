package com.ecmsp.productservice.domain;

import com.ecmsp.productservice.interfaces.AttributesAssignable;
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
@Builder(toBuilder = true)
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

    @Column(name = "value_text", columnDefinition = "text")
    private String valueText;

    @Column(name = "value_decimal", precision = 10, scale = 2)
    private BigDecimal valueDecimal;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "display_text", nullable = false)
    private String displayText;

    @Override
    public String toString() {
        return String.format("VariantProperty{id = %s}", id);
    }
}