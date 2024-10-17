package com.ecommerce.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.modelmapper.ModelMapper;

import com.ecommerce.ecommerce.exception.ApiException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.pojo.Cart;
import com.ecommerce.ecommerce.pojo.CartDTO;
import com.ecommerce.ecommerce.pojo.Category;
import com.ecommerce.ecommerce.pojo.Product;
import com.ecommerce.ecommerce.pojo.ProductDTO;
import com.ecommerce.ecommerce.pojo.ProductResponse;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.repository.CategoryRepository;
import com.ecommerce.ecommerce.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.io.*;


@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    FileService fileService;

    @Override
    public ProductDTO saveProductService(Long categoryId, ProductDTO productDTO) {
        Category category=categoryRepository.findById(categoryId)
                                            .orElseThrow(()->(new ResourceNotFoundException("category", "categoryId", categoryId)));
        
        boolean isProductPresent=false;
        List<Product> products=category.getProduct();
        for(int i=0;i<products.size();i++)
        {
            if(products.get(i).getProductName().equals(productDTO.getProductName()))
            {
                isProductPresent=true;
                break;
            }
        }
        if(!isProductPresent)
        {
            Product product=modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice=product.getPrice()-product.getPrice()*(product.getDiscount()*0.01);
            product.setSpecialPrice(specialPrice);
            Product savedProduct=productRepository.save(product);
            ProductDTO savedProductDTO=modelMapper.map(savedProduct,ProductDTO.class);
            return savedProductDTO;
        }
        else
        {
            throw new ApiException("Product already present");
        }
        
    }

    @Override
    public ProductResponse findAllProductsService(Integer pageNo,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNo,pageSize, sortByAndOrder);
        Page<Product> pageableProducts=productRepository.findAll(pageable);
       List<Product> products=pageableProducts.getContent();
       /*if(products.size()==0)
       throw new ApiException("Invalid exception");*/
       List<ProductDTO> productDTOs=products.stream().map(
                                                         product->modelMapper.map(product,ProductDTO.class))
                                                         .toList()
                                                            ;
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageableProducts.getNumber());
        productResponse.setPageSize(pageableProducts.getSize());
        productResponse.setTotalElements(pageableProducts.getTotalElements());
        productResponse.setTotalPages(pageableProducts.getTotalPages());
        productResponse.setLastPage(pageableProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategoryService(Long categoryId,int pageNo,int pageSize,String sortBy,String sortOrder) {
        

        Category categories=categoryRepository.findById(categoryId)
                            .orElseThrow(()->(new ResourceNotFoundException("category", "category_id", categoryId)));
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNo, pageSize, sortByAndOrder);
        
        Page<Product> pageableProducts=productRepository.findByCategoryOrderByPriceAsc(categories,pageable);
        List<Product> products=pageableProducts.getContent();
        if(products.size()==0)
        throw new ApiException("No product in category");
        List<ProductDTO> productDTOs=products.stream().map(
                                                         product->modelMapper.map(product,ProductDTO.class))
                                                         .toList()
                                                            ;
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageableProducts.getNumber());
        productResponse.setPageSize(pageableProducts.getSize());
        productResponse.setTotalElements(pageableProducts.getTotalElements());
        productResponse.setTotalPages(pageableProducts.getTotalPages());
        productResponse.setLastPage(pageableProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeywordService(String keywords,int pageNo,int pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNo, pageSize, sortByAndOrder);
        
        Page<Product> pageableProducts=productRepository.findByProductNameLikeIgnoreCase('%'+keywords+'%',pageable);
        List<Product> products=pageableProducts.getContent();
        if(products.size()==0)
        throw new ApiException("No product matches with keyword");
        List<ProductDTO> productDTOs=products.stream().map(
                                                         product->modelMapper.map(product,ProductDTO.class))
                                                         .toList()
                                                            ;
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageableProducts.getNumber());
        productResponse.setPageSize(pageableProducts.getSize());
        productResponse.setTotalElements(pageableProducts.getTotalElements());
        productResponse.setTotalPages(pageableProducts.getTotalPages());
        productResponse.setLastPage(pageableProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProductService(Long productId, ProductDTO productDTO) {
      Product productDetails=productRepository.findById(productId)
                                            .orElseThrow(()->new ResourceNotFoundException("product", "productId", productId));
        Product product=modelMapper.map(productDTO,Product.class);
        productDetails.setProductName(product.getProductName());
        productDetails.setDescription(product.getDescription());
        productDetails.setQuantity(product.getQuantity());
        productDetails.setDiscount(product.getDiscount());
        productDetails.setPrice(product.getPrice());
        double specialPrice=product.getPrice()-product.getPrice()*(product.getDiscount()*0.01);
        productDetails.setSpecialPrice(specialPrice);
        Product savedProductsDetails=productRepository.save(productDetails);

        List<Cart> carts=cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs=carts.stream().map(cart->{
            CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

            List<ProductDTO> products=cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();

            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();

        cartDTOs.forEach(cart->cartService.updateProductInCarts(cart.getCartId(),productId));
        return modelMapper.map(savedProductsDetails,ProductDTO.class);

       /*  ProductDTO updatedProductDTO=modelMapper.map(savedProductsDetails,ProductDTO.class);
        return updatedProductDTO;*/

    }

    @Override
    public ProductDTO deleteProductService(Long productId) {
        Product product=productRepository.findById(productId)
                                    .orElseThrow(()->(new ResourceNotFoundException("product", "productId", productId)));
        List<Cart> carts=cartRepository.findCartsByProductId(productId);
        carts.forEach(cart->cartService.deleteProductFromCart(cart.getCartId(), productId));
        productRepository.delete(product);
        ProductDTO deletedProductDTO=modelMapper.map(product, ProductDTO.class);
        return deletedProductDTO;

    }

    @Override
    public ProductDTO updateProductImageService(long productId, MultipartFile image) throws IOException {
        Product product=productRepository.findById(productId)
                            .orElseThrow(()->new ResourceNotFoundException("product", "productId", productId));
        String path="images/";
        String fileName=fileService.uploadImage(path,image);
        product.setImage(fileName);
        Product updatedProduct=productRepository.save(product);
        ProductDTO productDTO=modelMapper.map(updatedProduct,ProductDTO.class);
        return productDTO;
    }


    
    
}
