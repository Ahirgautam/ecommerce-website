package com.ecommerce.ecommerce_site.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductVariantFlatDTO {
    private String productName;
    private String description;
    private String category;
    private String brand;
    private String sku;
    private Double basePrice;
    private Double discountPrice;
    private Integer stock;
    private List<VariantAttributeDTO> attributes;
    private List<String> images;
}
