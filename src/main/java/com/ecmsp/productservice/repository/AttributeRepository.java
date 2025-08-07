package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Attribute;
import com.ecmsp.productservice.domain.AttributeDataType;
import com.ecmsp.productservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

    /**
     * Finds attributes that are filterable, representing general properties not tied to any specific product.
     * @param filterable whether attribute is generic
     * @return list of filterable, generic attributes
     */
    List<Attribute> findByFilterable(boolean filterable);

    /**
     * Finds attributes, that are not specific, based on category
     * @param category category entity
     * @param filterable whether attribute is generic
     * @return list of filterable attributes of category
     */
    List<Attribute> findByCategoryAndFilterable(Category category, boolean filterable);

    /**
     * Find attributes by data-type, for example: TEXT, NUMBER, BOOLEAN
     * @param dataType attribute's data type
     * @return list of attributes of specific data type
     */
    List<Attribute> findByDataType(AttributeDataType dataType);
}
