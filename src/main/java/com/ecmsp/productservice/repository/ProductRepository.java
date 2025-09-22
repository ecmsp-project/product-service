package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Finds products that have the specified category
     * @param category category
     * @return list of products with the given category
     */
    List<Product> findByCategory(Category category);

    /**
     * Finds products whose info map contains a specified key.
     * @param key info-map's key
     * @return list of products
     */
    @Query(value = "SELECT * FROM products p WHERE jsonb_exists(p.info, :key)", nativeQuery = true)
    List<Product> findByInfoKey(@Param("key") String key);

    /**
     * Finds products, which belong to specified category and whose info map contains a specified key
     * @param categoryId id of category
     * @param key info-map's key
     * @return
     */
    @Query(value = "SELECT * FROM products p WHERE p.category_id = :categoryId AND jsonb_exists(p.info, :key)", nativeQuery = true)
    List<Product> findByCategoryAndInfoKey(@Param("categoryId") UUID categoryId, @Param("key") String key);

    List<Product> findByApproximatePriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByDeliveryPriceBetween(BigDecimal deliveryPriceAfter, BigDecimal deliveryPriceBefore);

    Page<Product> findByCategory(Category category, Pageable pageable);
}
