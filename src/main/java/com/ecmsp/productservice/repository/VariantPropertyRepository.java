package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyOption;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantProperty;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantPropertyRepository extends JpaRepository<VariantProperty, UUID> {

    List<VariantProperty> findByVariant(Variant variant);
    List<VariantProperty> findByVariantId(UUID id);

    List<VariantProperty> findByProperty(Property property);
    List<VariantProperty> findByPropertyId(UUID id);

    List<VariantProperty> findByPropertyOption(PropertyOption propertyOption);
    List<VariantProperty> findByPropertyOptionId(UUID id);

    Optional<VariantProperty> findByVariantAndProperty(Variant variant, Property property);
}
