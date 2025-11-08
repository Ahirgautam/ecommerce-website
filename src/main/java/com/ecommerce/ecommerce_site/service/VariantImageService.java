package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.VariantImages;
import com.ecommerce.ecommerce_site.repo.VariantsImagesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VariantImageService {

    @Autowired
    private VariantsImagesRepo variantsImagesRepo;

    public void setVariantImage(String url, int order, ProductVariants variant){
        VariantImages variantImages = new VariantImages();
        variantImages.setProductVariant(variant);
        variantImages.setImg_url(url);
        variantImages.setDisplay_order(order);

        variantsImagesRepo.save(variantImages);
        ResponseEntity.ok("Image is stored");
    }
}
