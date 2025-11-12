package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Finds a category by its exact name.
     * @param name the name of the category
     * @return an Optional containing category if found, or empty if no category exists with the given name
     */
    Optional<Category> findByName(String name);

    /**
     * Find categories that have the specified parent category
     * @param parentCategory the parent category to filter by (can be null)
     * @return list of categories with the given parent
     */
    List<Category> findByParentCategory(Category parentCategory);

    List<Category> findByParentCategory_Id(UUID parentCategoryId);

    /**
     * Finds a category by its name and parent category.
     * This can be used to check if a category name is unique within the same level of the category tree.
     * @param name the name of the category to find
     * @param parentCategory the parent category (can be null)
     * @return an Optional containing the matching category if found, or empty if no match exists
     */
    Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory = :category")
    boolean hasSubCategories(Category category);

    Optional<Category> getCategoryByName(String name);
}
