package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.DefaultPropertyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DefaultPropertyOptionRepository extends JpaRepository<DefaultPropertyOption, UUID> {

    List<DefaultPropertyOption> findByProperty(Property property);
    List<DefaultPropertyOption> findByPropertyId(UUID propertyId);


}
