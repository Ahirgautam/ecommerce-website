package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.ProductVariantDTO;
import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.model.VariantImages;
import com.ecommerce.ecommerce_site.repo.ProductVariantsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductVariantsService {

    @Autowired
    private ProductVariantsRepo productVariantsRepo;
    @Autowired
    private VariantAttributeService variantAttributeService;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private VariantImageService variantImageService;

    public void setVariants(List<ProductVariantDTO> productVariantsDto, Products product, Map<String, MultipartFile> variantImages, int defaultImageCount) {

        List<String> savedDefaultImages = new ArrayList<>();
        for(int i = 0; i < defaultImageCount; i++){
            String key = "Default["+i+"]";
            MultipartFile image = variantImages.get(key);
            if(image == null) continue;
            try{
                String newImageName = imageStorageService.saveImage(image);
                savedDefaultImages.add(newImageName);
            }
            catch (IOException ioException){
                System.out.println(ioException);
            }
        }
        for(ProductVariantDTO variant : productVariantsDto){
            ProductVariants productVariant = new ProductVariants();
            productVariant.setProduct(product);
            productVariant.setName(variant.getName());
            productVariant.setDiscountedPrice(variant.getDiscountPrice());
            productVariant.setBasePrice(variant.getBasePrice());
            productVariant.setSku(variant.getSku());
            productVariant.setStock(variant.getStock());

            ProductVariants insertedVariant = productVariantsRepo.save(productVariant);
            variantAttributeService.setVariantAttribute(variant.getAttributes(), insertedVariant);
            int i = 0;
            for(i = 0; i < variant.getImages().size(); i++) {
                String key = "variantImages[" + variant.getSku() + "]" + "[" + i + "]";
                MultipartFile image = variantImages.get(key);
                try{
                    String newImageName = imageStorageService.saveImage(image);
                    variantImageService.setVariantImage(newImageName, i, insertedVariant);
                }catch (IOException exception){
                    System.out.println(exception);
                }
            }
            for(int j = 0; j < savedDefaultImages.size(); j++){
                    variantImageService.setVariantImage(savedDefaultImages.get(j), i+j, insertedVariant);
            }
        }

        ResponseEntity.ok("done");
    }

    public boolean getAllSku(String sku){
        return productVariantsRepo.existsBySku(sku);
    }

    public void deleteVariant(long id){

        Optional<ProductVariants> productVariants = productVariantsRepo.findById(id);
        if(productVariants.isEmpty()) {
            ResponseEntity.notFound().build();
            return;
        }
        Set<String> imageNames = productVariants.get().getImages()
                .stream()
                .map(VariantImages::getImg_url)
                .collect(Collectors.toSet());

        for(String imageName : imageNames){
            imageStorageService.deleteImage(imageName);
        }
        productVariantsRepo.deleteById(id);

        ResponseEntity.ok().build();
    }
}
