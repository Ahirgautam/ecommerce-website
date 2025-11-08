package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.dto.CategoryDTO;
import com.ecommerce.ecommerce_site.model.Categories;
import com.ecommerce.ecommerce_site.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/api/categories")
    public List<CategoryDTO> getAllCategories(){
        return categoryService.getCategories();
    }
    @GetMapping("/api/categories/parent")
    public List<Categories> getAllParentCategories(){
        return categoryService.getAllParentCategories();
    }

    @PostMapping("/api/categories")
    public ResponseEntity<?> setCategory(@RequestParam String categoryName, @RequestParam String parentCategory){
        return categoryService.setCategory(categoryName, parentCategory);
    }
}
