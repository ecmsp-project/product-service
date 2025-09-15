package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyOptionRepository extends JpaRepository<PropertyOption, UUID> {

    List<PropertyOption> findByProperty(Property property);
    List<PropertyOption> findByPropertyId(UUID propertyId);


}
