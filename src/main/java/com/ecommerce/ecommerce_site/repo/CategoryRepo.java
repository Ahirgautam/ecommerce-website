package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Categories, Long> {
    Categories findByName(String name);

    @Query("SELECT c FROM Categories c WHERE c.parent IS NULL")
    List<Categories> getAllParentCategories();
}
