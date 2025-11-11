package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Property;
import com.ecmsp.productservice.domain.PropertyDataType;
import com.ecmsp.productservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findByIdAndRequired(UUID id, boolean required);

    List<Property> findByIdAndCategory(UUID id, Category category);

    /**
     * Find properties by data-type, for example: TEXT, NUMBER, BOOLEAN, DATE
     * @param dataType property's data type
     * @return list of properties of specific data type
     */
    List<Property> findByDataType(PropertyDataType dataType);

    @Query("""
        SELECT DISTINCT p
        FROM Property p
        JOIN FETCH p.defaultPropertyOptions
        WHERE p.categoryId = :categoryId
    """)
    List<Property> findAllWithDefaultPropertyOptionsByCategoryId(@Param("categoryId") UUID categoryId);
}
