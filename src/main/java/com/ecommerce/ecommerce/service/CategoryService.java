package com.ecommerce.ecommerce.service;
import com.ecommerce.ecommerce.pojo.CategoryDTO;
import com.ecommerce.ecommerce.pojo.CategoryResponse;

public interface CategoryService {
     public CategoryResponse getAllCategories(int pageNumber,int pageSize,String sortBy,String sortOrder);
     public CategoryDTO addCategory(CategoryDTO categoryDTO);
     public CategoryDTO deleteCategory(Long categoryId);
     public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

    
}
