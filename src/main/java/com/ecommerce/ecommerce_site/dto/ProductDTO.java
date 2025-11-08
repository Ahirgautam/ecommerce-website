package com.ecommerce.ecommerce_site.dto;

import java.util.List;

public class ProductDTO {
    private long productId;
    private String productName;
    private String description;
    private String category;
    private String brand;
    private int defaultImageCount;
    private List<ProductVariantDTO> variants;

    public ProductDTO(){}
    public ProductDTO(long productId, String productName, String description, String category, String brand, List<ProductVariantDTO> variants) {
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.variants = variants;
        this.productId = productId;

    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public List<ProductVariantDTO> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDTO> variants) { this.variants = variants; }

    public int getDefaultImageCount() {
        return defaultImageCount;
    }

    public void setDefaultImageCount(int defaultImageCount) {
        this.defaultImageCount = defaultImageCount;
    }
}
