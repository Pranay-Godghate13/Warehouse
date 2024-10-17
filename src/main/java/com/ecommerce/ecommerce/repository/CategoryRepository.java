package com.ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.pojo.Cart;
import com.ecommerce.ecommerce.pojo.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>{

    Category findByCategoryName(String categoryName);

    //List<Cart> findCartsByProductId(Long productId);
    
}
