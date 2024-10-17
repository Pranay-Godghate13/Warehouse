package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.pojo.CartDTO;

import jakarta.transaction.Transactional;


public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

}
