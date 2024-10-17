package com.ecommerce.ecommerce.controller;


import java.util.List;

import com.ecommerce.ecommerce.config.AppConstants;
import com.ecommerce.ecommerce.pojo.Category;
import com.ecommerce.ecommerce.pojo.CategoryDTO;
import com.ecommerce.ecommerce.pojo.CategoryResponse;
import com.ecommerce.ecommerce.service.CategoryService;

import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api")
public class CategoryController {
    
    @Autowired
    CategoryService categoryService;
  
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                        @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                        @RequestParam(name="sortBy",defaultValue=AppConstants.SORT_BY,required = false) String sortBy,
                                                        @RequestParam(name="sortOrder",defaultValue=AppConstants.SORT_ORDER,required = false) String sortOrder) {
        /*CategoryResponse categories=categoryService.getAllCategories();
        return new ResponseEntity(categories, HttpStatus.OK);*/
        CategoryResponse category=categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<CategoryResponse>(category, HttpStatus.OK);
    }
    
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category)
    {
        CategoryDTO savedcategoryDTO=categoryService.addCategory(category);
        return new ResponseEntity<>(savedcategoryDTO,HttpStatus.CREATED);
    }
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deletedCategoryDTO=categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
    }
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
     
        CategoryDTO savedCategoryDTO=categoryService.updateCategory(categoryDTO,categoryId);
         return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
      
        
    }
}
