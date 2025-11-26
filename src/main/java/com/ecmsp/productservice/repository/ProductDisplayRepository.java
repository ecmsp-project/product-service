package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductDisplayRepository extends JpaRepository<Variant, UUID> {

    @Query(
            value = """
        SELECT DISTINCT ON (products.id) variants.*
        FROM products
        JOIN variants on products.id = variants.product_id
        WHERE products.category_id = :categoryId
        ORDER BY products.id, variants.price
    """,
            nativeQuery = true
    )
    Page<Variant> findOneVariantPerProductByCategoryId(UUID categoryId, Pageable pageable);

}
