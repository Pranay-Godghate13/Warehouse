package com.ecommerce.ecommerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.ecommerce.config.AppConstants;
import com.ecommerce.ecommerce.pojo.ProductDTO;
import com.ecommerce.ecommerce.pojo.ProductResponse;
import com.ecommerce.ecommerce.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageNo",defaultValue=AppConstants.PAGE_NUMBER,required = false) Integer pageNo,
                                                            @RequestParam(name = "pageSize",defaultValue=AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required =false)String sortBy,
                                                            @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_ORDER,required = false)String sortOrder){
        ProductResponse productResponse=productService.findAllProductsService(pageNo,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,@RequestParam(name = "pageNo",defaultValue=AppConstants.PAGE_NUMBER,required = false) Integer pageNo,
                                                                 @RequestParam(name = "pageSize",defaultValue=AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required =false)String sortBy,
                                                                 @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_ORDER,required = false)String sortOrder) {
        ProductResponse productResponse=productService.getProductsByCategoryService(categoryId,pageNo,pageSize,sortBy,sortOrder);
        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keywords}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keywords,@RequestParam(name = "pageNo",defaultValue=AppConstants.PAGE_NUMBER,required = false) Integer pageNo,
                                                                @RequestParam(name = "pageSize",defaultValue=AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required =false)String sortBy,
                                                                @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_ORDER,required = false)String sortOrder)
    {
        ProductResponse productResponse=productService.getProductsByKeywordService(keywords,pageNo,pageSize,sortBy,sortOrder);
        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
    }
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO=productService.updateProductService(productId,productDTO);
        return new ResponseEntity<ProductDTO>(updatedProductDTO, HttpStatus.OK);
    }
    @PutMapping("products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable long productId, 
                                    @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO productDTO=productService.updateProductImageService(productId,image);
        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }
    
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> saveProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long categoryId)
    {
        ProductDTO savedProductDTO=productService.saveProductService(categoryId,productDTO);
        return new ResponseEntity<ProductDTO>(productDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId)
    {
        ProductDTO productDTO=productService.deleteProductService(productId);
        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }
}
