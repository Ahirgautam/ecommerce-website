package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.dto.ProductDTO;
import com.ecommerce.ecommerce_site.model.LikedProducts;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.model.UserPrincipal;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.ProductsRepo;
import com.ecommerce.ecommerce_site.service.LikeProductService;
import com.ecommerce.ecommerce_site.service.ProductsService;
import com.ecommerce.ecommerce_site.service.UserRegisterService;
import com.ecommerce.ecommerce_site.service.UserService;
import com.ecommerce.ecommerce_site.utils.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;



@RestController
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private LikeProductService likeProductService;
    @Autowired
    private UserService userService;

    @GetMapping("/api/products/favorite")
    public ResponseEntity<?> getFavoriteProducts(){
        Users users = userService.getCurrentUser();
        return ResponseEntity.ok(productsService.getFavoriteProducts(users));
    }

    @PostMapping(value = "/api/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveProduct(
            @RequestPart("product") String productJson,
            @RequestParam Map<String, MultipartFile> variantImages) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ProductDTO product = mapper.readValue(productJson, ProductDTO.class);

        productsService.setProduct(product, variantImages);
        return ResponseEntity.ok("Product saved successfully");
    }


    @GetMapping("/api/products")
    public PageResponse<ProductDTO> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) boolean needLiked,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ProductDTO> page =  productsService.getPagedProductsNested(search, categoryId, pageable);
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    @DeleteMapping("/api/product/{id}")
    public void deleteProduct(@PathVariable String id){
        productsService.deleteProduct(Long.parseLong(id));
        ResponseEntity.ok();
    }

    @GetMapping("/api/product/filter")
    public PageResponse<ProductDTO> filterProducts(
            @RequestParam List<Long> categories,
            @RequestParam List<String> brands,
            @RequestParam double minPrice,
            @RequestParam double maxPrice,
            @RequestParam(required = false) List<String>attributeValue,
            @PageableDefault(size = 10) Pageable pageable
    ){


        Page<ProductDTO> page = productsService.filterProducts(categories,brands,minPrice,maxPrice, attributeValue, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @GetMapping("/api/products/minMaxPrice")
    public Map<String, Double> minMaxPrice(){
        Map<String, Double> price =  productsService.minimumAndMaximumPrice();

        return price;
    }

    @GetMapping("/api/products/{id}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable Long id){

        Users users = userService.getCurrentUser();
        if(users == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Products product = productsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        boolean liked = likeProductService.toggleLike(users, product);

        return ResponseEntity.ok(Map.of("liked", liked));
    }
    public record PageResponse<T>(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {}
}






