package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Product;
import com.ecmsp.productservice.domain.Variant;
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
    void reserveVariant(@Param("variantId") UUID variantId, @Param("quantity") int quantity);
}
