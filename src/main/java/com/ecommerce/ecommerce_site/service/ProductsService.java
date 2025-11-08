package com.ecommerce.ecommerce_site.service;


import com.ecommerce.ecommerce_site.dto.PriceRange;
import com.ecommerce.ecommerce_site.dto.ProductDTO;
import com.ecommerce.ecommerce_site.dto.ProductVariantDTO;
import com.ecommerce.ecommerce_site.dto.VariantAttributeDTO;
import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.model.VariantImages;
import com.ecommerce.ecommerce_site.repo.CategoryRepo;
import com.ecommerce.ecommerce_site.repo.LikedProductsRepo;
import com.ecommerce.ecommerce_site.repo.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ProductsService {
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductVariantsService productVariantsService;
    @Autowired
    private LikedProductsRepo likedProductsRepo;

    public List<Products> getAllProducts() {
        return productsRepo.findAll();
    }

    public List<Products> getProductsByCategory(Long categoryId) {
        return productsRepo.findByCategory_CategoryId(categoryId);
    }

    public List<Products> searchProducts(String keyword) {
        return productsRepo.findByNameContainingIgnoreCase(keyword);
    }

    public ResponseEntity<?> setProduct(ProductDTO productDTO, Map<String, MultipartFile> variantImages){
        Products products = new Products();
        products.setCategory(categoryRepo.findById(Long.parseLong(productDTO.getCategory())).get());
        products.setName(productDTO.getProductName());
        products.setDescription(productDTO.getDescription());
        products.setBrandName(productDTO.getBrand());

        Products insertedProduct = productsRepo.save(products);

        productVariantsService.setVariants(productDTO.getVariants(), insertedProduct, variantImages,  productDTO.getDefaultImageCount());
        return  ResponseEntity.ok("done");
    }

    public List<String>getAllBrandName(){
        return productsRepo.getAllBrandName();
    }

    public Page<ProductDTO> getPagedProductsNested(
            String search,
            Long categoryId,
            Pageable pageable) {

        Page<Long> productIdsPage = productsRepo.findProductIdsForPage(search, categoryId, pageable);

        if (productIdsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<ProductDTO> dtos = findProductByIds(productIdsPage.getContent());
        return new PageImpl<>(dtos, pageable, productIdsPage.getTotalElements());
    }

    public void deleteProduct(long id){
        Optional<Products> products = productsRepo.findById(id);
        if(products.isEmpty()){
            ResponseEntity.badRequest().build();
        }
        products.ifPresent(product -> {
            for (ProductVariants variant : product.getVariants()) {
                productVariantsService.deleteVariant(variant.getVariant_id());
            }
        });
        productsRepo.deleteById(id);
        ResponseEntity.ok().build();
    }


    public Page<ProductDTO> filterProducts(List<Long> categories, List<String> brands, double minPrice, double maxPrice, List<String> attributeValue, Pageable pageable) {
        Page<Long> productIdsPage = productsRepo.filterProduct(categories, brands,minPrice,maxPrice,attributeValue, pageable);

        if(productIdsPage.isEmpty()){
            return Page.empty(pageable);
        }
        List<ProductDTO> dtos = findProductByIds(productIdsPage.getContent());
        return new PageImpl<>(dtos, pageable, productIdsPage.getTotalElements());
    }

    public List<ProductDTO> findProductByIds(List<Long> ids){
        List<Products> products = productsRepo.findAllByIdWithDetails(ids);
        List<ProductDTO> dtos = products.stream()
                .map(p -> new ProductDTO(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.getCategory().getName(),
                        p.getBrandName(),
                        p.getVariants().stream()
                                .map(v -> new ProductVariantDTO(
                                        v.getName(),
                                        v.getVariant_id(),
                                        v.getSku(),
                                        v.getBasePrice(),
                                        v.getDiscountedPrice(),
                                        v.getStock(),
                                        v.getVariantAttributes().stream()
                                                .map(attr -> new VariantAttributeDTO(
                                                        attr.getAttribute_name(),
                                                        attr.getAttribute_value()
                                                )).toList(),
                                        v.getImages().stream()
                                                .map(VariantImages::getImg_url)
                                                .toList()
                                )).toList()
                )).toList();
        return dtos;
    }


    public Map<String, Double> minimumAndMaximumPrice(){
        PriceRange re = productsRepo.findMinimumMaximumPrice();
        Map<String, Double> priceRange = new HashMap<>();
        priceRange.put("minimum", re.getMinimum());
        priceRange.put("maximum", re.getMaximum());
        return priceRange;
    }

    public List<ProductDTO> getFavoriteProducts(Users users){
        List<Long> productIds = likedProductsRepo.findByUsers(users);
        return findProductByIds(productIds);
    }

}
