package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantPropertyRepository extends JpaRepository<VariantProperty, UUID> {

    List<VariantProperty> findByVariant(Variant variant);
    List<VariantProperty> findByVariantId(UUID id);

    List<VariantProperty> findByProperty(Property property);
    List<VariantProperty> findByPropertyId(UUID id);

    Optional<VariantProperty> findByVariantAndProperty(Variant variant, Property property);

    VariantProperty findByVariantIdAndPropertyId(UUID id, UUID propertyId);

    List<VariantProperty> findByVariantIdAndPropertyIdIn(UUID variantId, List<UUID> properties);

    List<VariantProperty> findByVariantIdAndPropertyRole(UUID id, PropertyRole role);

    List<VariantProperty> findByVariantIdInAndPropertyRole(List<UUID> ids, PropertyRole role);

    List<VariantProperty> findByVariantInAndPropertyRole(List<Variant> variants, PropertyRole role);
}
