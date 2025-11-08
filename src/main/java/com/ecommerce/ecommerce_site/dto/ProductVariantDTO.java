package com.ecommerce.ecommerce_site.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductVariantDTO {
    private long id;
    private String name;
    private String sku;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private Long stock;
    private List<VariantAttributeDTO> attributes;
    private List<String> images;  // only filenames from JSON
    public ProductVariantDTO(){}
    public ProductVariantDTO(String name,long id,String sku, BigDecimal basePrice, BigDecimal discountPrice, Long stock,
                             List<VariantAttributeDTO> attributes, List<String> images) {
        this.name = name;
        this.id = id;
        this.sku = sku;
        this.basePrice = basePrice;
        this.discountPrice = discountPrice;
        this.stock = stock;
        this.attributes = attributes;
        this.images = images;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public Long getStock() { return stock; }
    public void setStock(Long stock) { this.stock = stock; }

    public List<VariantAttributeDTO> getAttributes() { return attributes; }
    public void setAttributes(List<VariantAttributeDTO> attributes) { this.attributes = attributes; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
