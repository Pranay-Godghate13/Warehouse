package com.ecommerce.ecommerce.repository;



import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.pojo.Category;
import com.ecommerce.ecommerce.pojo.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category categories, Pageable pageable);

    Page<Product> findByProductNameLikeIgnoreCase(String string, Pageable pageable);
    
}
