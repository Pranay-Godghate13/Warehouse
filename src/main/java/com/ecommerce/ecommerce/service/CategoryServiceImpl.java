package com.ecommerce.ecommerce.service;


import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;


import com.ecommerce.ecommerce.exception.ApiException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.pojo.Category;
import com.ecommerce.ecommerce.pojo.CategoryDTO;
import com.ecommerce.ecommerce.pojo.CategoryResponse;
import com.ecommerce.ecommerce.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;
    

    @Override
    public CategoryResponse getAllCategories(int pageNumber,int pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                                                             ?Sort.by(sortBy).ascending()
                                                             :Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageable);
        List<Category> allCategories=categoryPage.getContent();
        CategoryResponse categoryResponse=new CategoryResponse();
        if(allCategories.size()==0)
        throw new ApiException("There are no category present, create category!");

        List<CategoryDTO> categoryDTO=allCategories.stream()
                            .map(element->modelMapper.map(element, CategoryDTO.class))
                            .toList();
        categoryResponse.setContents(categoryDTO);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO,Category.class);
        Category categoryName=categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryName!=null)
        throw new ApiException("Category name is already present");
        Category savedCategory= categoryRepository.save(category);
        CategoryDTO savCategoryDTO=modelMapper.map(savedCategory, CategoryDTO.class);
        return savCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        //List<Category> categories=categoryRepository.findAll();
        Optional<Category> existingCategory=categoryRepository.findById(categoryId);
        Category savedCategory=existingCategory
                                .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));

        categoryRepository.delete(savedCategory);
        CategoryDTO deletedCategoryDTO=modelMapper.map(savedCategory,CategoryDTO.class);
        return deletedCategoryDTO;
        /*for(Category category:categories)
        {
            if(category.getCategoryId()==categoryId)
            {
                categoryRepository.delete(category);
                return "The category with category id "+categoryId+" is removed";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");*/
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Optional<Category> existingCategory=categoryRepository.findById(categoryId);
        Category savedCategory=existingCategory
                                .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
        
        savedCategory.setCategoryName(categoryDTO.getCategoryName());
        categoryRepository.save(savedCategory);
        CategoryDTO savedCategoryDTO=modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
        //List<Category> categories=categoryRepository.findAll();
        
        /*Optional<Category> optionalCategory=categories.stream()
            .filter(c->c.getCategoryId().equals(categoryId))
            .findFirst();*/
        
        /*if(optionalCategory.isPresent())
        {
            Category existinigCategory=optionalCategory.get();
            existinigCategory.setCategoryName(category.getCategoryName());
            return categoryRepository.save(existinigCategory);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the id");
        }*/
        
    }
    
    
}
