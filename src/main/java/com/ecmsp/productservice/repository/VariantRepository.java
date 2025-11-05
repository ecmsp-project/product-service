package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {

    List<Variant> findByProduct(Product product);
    List<Variant> findByProductId(UUID productId);

    List<Variant> findByStockQuantityGreaterThan(int quantity);

    List<Variant> findByPriceBetween(BigDecimal min, BigDecimal max);


    @Modifying
    @Query("UPDATE Variant v SET v.stockQuantity = v.stockQuantity - :quantity WHERE v.id = :variantId AND v.stockQuantity >= :quantity")
    int reserveVariant(@Param("variantId") UUID variantId, @Param("quantity") int quantity);

//    @Query("""
//        SELECT v FROM Variant v
//        WHERE v.product.category.id = :categoryId
//            AND v.createdAt = (
//                SELECT MAX(v2.createdAt)
//                FROM Variant v2
//                WHERE v2.product.id = v.product.id
//            )
//    """)
    @Query(
            value = """
            SELECT * FROM (
                SELECT v.*,
                       ROW_NUMBER() OVER (PARTITION BY v.product_id ORDER BY v.created_at DESC, v.id DESC) as rn
                FROM variants v
                JOIN products p ON p.id = v.product_id
                WHERE p.category_id = :categoryId
            ) t
            WHERE t.rn = 1
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT 1
                FROM variants v
                JOIN products p ON p.id = v.product_id
                WHERE p.category_id = :categoryId
                GROUP BY v.product_id
            ) t
            """
            ,
            nativeQuery = true
    )
    Page<Variant> getOneVariantPerProductByCategoryId(UUID categoryId, Pageable pageable);
}
