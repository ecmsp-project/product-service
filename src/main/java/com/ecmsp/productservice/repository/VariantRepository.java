package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {

    List<Variant> findByProduct(Product product);

    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId")
    List<Variant> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT v FROM Variant v WHERE v.product.productId = :productId")
    List<Variant> findByProductProductId(@Param("productId") Integer productId);

    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId AND v.stockQuantity - v.reservedQuantity >= :minQuantity")
    List<Variant> findAvailableVariantsByProductId(@Param("productId") UUID productId, @Param("minQuantity") int minQuantity);

    @Query("SELECT v FROM Variant v WHERE v.product.productId = :productId AND v.stockQuantity - v.reservedQuantity >= :minQuantity")
    List<Variant> findAvailableVariantsByProductProductId(@Param("productId") Integer productId, @Param("minQuantity") int minQuantity);

    @Modifying
    @Query("UPDATE Variant v SET v.reservedQuantity = v.reservedQuantity + :quantity WHERE v.id = :variantId")
    void increaseReservedQuantity(@Param("variantId") UUID variantId, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE Variant v SET v.reservedQuantity = v.reservedQuantity - :quantity WHERE v.id = :variantId AND v.reservedQuantity >= :quantity")
    void decreaseReservedQuantity(@Param("variantId") UUID variantId, @Param("quantity") int quantity);
}
