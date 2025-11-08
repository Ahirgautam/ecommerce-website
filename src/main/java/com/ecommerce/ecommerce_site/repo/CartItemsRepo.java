package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.dto.CartItemsDTO;
import com.ecommerce.ecommerce_site.model.CartItems;
import com.ecommerce.ecommerce_site.model.Carts;
import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepo extends JpaRepository<CartItems, Long> {


    Optional<CartItems> findByProductsAndProductVariantsAndCart(Products product, ProductVariants productVariants, Carts cart);

    @Query("""
           SELECT new com.ecommerce.ecommerce_site.dto.CartItemsDTO(ci.id, ci.quantity, ci.priceAtAddition, p.name, p.brandName, p.category.name, vi.img_url, pv.stock)
           FROM CartItems ci 
           LEFT JOIN ci.products p
           LEFT JOIN ci.productVariants pv
           LEFT JOIN pv.images vi
           WHERE ci.cart.id = :id AND vi.display_order = 0
            """)
    List<CartItemsDTO> fetchAllCartItems(@Param("id") Long id);
}
