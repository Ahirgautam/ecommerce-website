package com.ecommerce.ecommerce_site.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String brandName;
    // many products belong to one category
    //category_id foreign key to categorise table
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    // one product can have many variants
    //delete variant when associated product get deleted
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ProductVariants> variants;

    @OneToMany(mappedBy = "products", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CartItems> cartItems;

    @OneToMany(mappedBy = "products", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItems> orderItems ;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikedProducts> likedByUsers = new HashSet<>();


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Categories getCategory() { return category; }
    public void setCategory(Categories category) { this.category = category; }

    public Set<ProductVariants> getVariants() { return variants; }
    public void setVariants(Set<ProductVariants> variants) { this.variants = variants; }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
