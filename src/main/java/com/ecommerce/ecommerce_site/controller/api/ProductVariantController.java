package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.service.ProductVariantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ProductVariantController {

    @Autowired
    private ProductVariantsService productVariantsService;

    @GetMapping("/api/allsku/{name}")
    public ResponseEntity<Boolean> checkSku(@PathVariable String name){
        return ResponseEntity.ok(productVariantsService.getAllSku(name));
    }

    @DeleteMapping("/api/variant/{id}")
    public ResponseEntity<Boolean> deleteVariant(@PathVariable String id){
        System.out.println("hii");
        productVariantsService.deleteVariant(Long.parseLong(id));
        return ResponseEntity.ok(true);
    }
}
