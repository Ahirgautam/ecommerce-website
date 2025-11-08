package com.ecommerce.ecommerce_site.repo;


import com.ecommerce.ecommerce_site.model.LikedProducts;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.model.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedProductsRepo extends JpaRepository<LikedProducts, Long> {
    Optional<LikedProducts> findByUsersAndProducts(Users users, Products products);
    void deleteByUsersAndProducts(Users users, Products products);
    List<LikedProducts> findByUsersAndProducts_ProductIdIn(Users users, List<Long> productIds);

    @Query("SELECT p.products.id FROM LikedProducts p WHERE p.users = :user ")
    List<Long> findByUsers(@Param("user") Users users);
}
