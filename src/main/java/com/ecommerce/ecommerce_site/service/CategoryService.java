package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.CategoryDTO;
import com.ecommerce.ecommerce_site.model.Categories;
import com.ecommerce.ecommerce_site.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    public List<CategoryDTO> getCategories(){

        return categoryRepo.findAll().stream().map(cat -> new CategoryDTO(cat.getId(), cat.getName(),
                cat.getParent() != null ? cat.getParent().getId() : null )).toList();
    }

    public List<Categories> getAllParentCategories(){
        return categoryRepo.getAllParentCategories();
    }

    public ResponseEntity<?> setCategory(String categoryName, String parentCategory) {
        if(categoryName == null) return ResponseEntity.badRequest().body("Category name is required");;
        Categories categories = new Categories();
        categories.setName(categoryName);
        if(parentCategory.isEmpty()){
            categories.setParent(null);
        }
        else{
            categories.setParent(categoryRepo.findById(Long.parseLong(parentCategory)).orElse(null));
        }
        return new ResponseEntity<>(categoryRepo.save(categories), HttpStatus.CREATED);
    }
}
