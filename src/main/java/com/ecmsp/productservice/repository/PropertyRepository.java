package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    /**
     * Finds properties that are filterable, representing general properties not tied to any specific product.
     * @param filterable whether properties is generic
     * @return list of filterable, generic properties
     */
    List<Property> findByFilterable(boolean filterable);

    /**
     * Finds properties, that are not specific, based on category
     * @param category category entity
     * @param filterable whether property is generic
     * @return list of filterable properties of category
     */
    List<Property> findByCategoryAndFilterable(Category category, boolean filterable);

    /**
     * Find properties by data-type, for example: TEXT, NUMBER, BOOLEAN, DATE
     * @param dataType property's data type
     * @return list of properties of specific data type
     */
    List<Property> findByDataType(PropertyDataType dataType);
}
