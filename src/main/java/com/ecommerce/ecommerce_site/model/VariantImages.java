package com.ecommerce.ecommerce_site.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "variant_images")
public class VariantImages {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long image_id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    @JsonBackReference
    private ProductVariants productVariant;

    @Column(nullable = false)
    private String img_url;
    @Column(nullable = false)
    private int display_order;

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }

    public ProductVariants getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariants productVariant) {
        this.productVariant = productVariant;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }


}
