package com.ecommerce.ecommerce_site.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemsDTO {
    private Long id;
    private Integer quantity;
    private BigDecimal priceAtAddition;
    private String productName;
    private String brandName;
    private String category;
    private String image;
    private Long stock;
    public CartItemsDTO(Long id, Integer quantity, BigDecimal priceAtAddition, String productName, String brandName, String category, String image,Long stock) {
        this.id = id;
        this.quantity = quantity;
        this.priceAtAddition = priceAtAddition;
        this.productName = productName;
        this.brandName = brandName;
        this.category = category;
        this.image = image;
        this.stock = stock;
    }

    // Getters omitted for brevity
}
