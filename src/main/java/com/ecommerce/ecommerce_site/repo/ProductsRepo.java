package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.dto.PriceRange;
import com.ecommerce.ecommerce_site.model.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductsRepo extends JpaRepository<Products, Long> {
    @EntityGraph(attributePaths = {"variants"})
    Optional<Products> findById(Long id);

    List<Products> findByCategory_CategoryId(Long categoryId);

    // Find products by brand name
    List<Products> findByBrandName(String brandName);

    // Search products by name containing keyword (case insensitive)
    List<Products> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT p.brandName FROM Products p ORDER BY p.brandName ASC")
    List<String> getAllBrandName();

    @Query("""
        SELECT DISTINCT p.id 
        FROM Products p
        LEFT JOIN p.variants v
        WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) 
               OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    Page<Long> findProductIdsForPage(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    
    @Query("""
        SELECT  p FROM Products p
        LEFT JOIN FETCH p.variants v
        LEFT JOIN FETCH v.variantAttributes
        LEFT JOIN FETCH v.images
        WHERE p.id IN :ids
    """)
    List<Products> findAllByIdWithDetails(@Param("ids") List<Long> ids);


    @Query("""
    SELECT DISTINCT p.id 
    FROM Products p
    LEFT JOIN  p.variants v
    LEFT JOIN  v.variantAttributes va
    WHERE (:#{#categories == null || #categories.isEmpty()} = true OR p.category.id IN :categories)
      AND (:#{#brand == null || #brand.isEmpty()} = true OR p.brandName IN :brand)
      AND v.discountedPrice BETWEEN :minVal AND :maxVal
      AND (:#{#attributeValues == null || #attributeValues.isEmpty()} = true OR va.attribute_value IN :attributeValues)
""")
    Page<Long> filterProduct(@Param("categories") List<Long> categories,
                                 @Param("brand") List<String> brand,
                                 @Param("minVal") double minVal,
                                 @Param("maxVal") double maxVal,
                                 @Param("attributeValues") List<String> attributeValues, Pageable pageable);


    @Query(value = """
           select min(v.discounted_price) as minimum, max(v.discounted_price) as maximum from product_variants v
            """, nativeQuery = true)
    PriceRange findMinimumMaximumPrice();
}

