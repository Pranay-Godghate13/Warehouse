package com.ecommerce.ecommerce.service;


import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.ecommerce.pojo.ProductDTO;
import com.ecommerce.ecommerce.pojo.ProductResponse;

public interface ProductService {

    ProductDTO saveProductService(Long categoryId, ProductDTO productDTO);

    ProductResponse findAllProductsService(Integer pageNo,Integer pageSize,String sortBy,String sortOrder);

    ProductResponse getProductsByCategoryService(Long categoryId,int pageNo,int pageSize,String sortBy,String sortOrder);

    ProductResponse getProductsByKeywordService(String keywords,int pageNo,int pageSize,String sortBy,String sortOrder);

    ProductDTO updateProductService(Long productId, ProductDTO productDTO);

    ProductDTO deleteProductService(Long productId);

    ProductDTO updateProductImageService(long productId, MultipartFile image) throws IOException;
    
}
