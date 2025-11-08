package com.ecommerce.ecommerce_site.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_variants")
public class ProductVariants {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long variant_id;
    private String name;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountedPrice;
    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;
    private long stock;

//    @Column(nullable = false, unique = true)
    private String sku;

    // one product can have many variants
    //product_id foreign key to product table
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Products product;

    // delete associated images in VariantImages table when a productVariant deletes
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<VariantImages> images;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<VariantAttribute> variantAttributes;

    @OneToMany(mappedBy = "productVariants", orphanRemoval = true)
    @JsonManagedReference
    private List<CartItems> cartItems;

    @OneToMany(mappedBy = "productVariants", orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItems> orderItems;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public long getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(long variant_id) {
        this.variant_id = variant_id;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public Set<VariantImages> getImages() {
        return images;
    }

    public void setImages(Set<VariantImages> images) {
        this.images = images;
    }

    public Set<VariantAttribute> getVariantAttributes() {
        return variantAttributes;
    }

    public void setVariantAttributes(Set<VariantAttribute> variantAttributes) {
        this.variantAttributes = variantAttributes;
    }
}
