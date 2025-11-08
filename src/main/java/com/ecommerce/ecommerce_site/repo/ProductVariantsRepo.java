package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantsRepo extends JpaRepository<ProductVariants, Long> {

    @Query("SELECT pv.sku FROM ProductVariants pv")
    List<String> findAllSkus();

    boolean existsBySku(String sku);

}
