package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.ecommerce.pojo.CartItems;

public interface CartItemsRepository extends JpaRepository<CartItems,Long> {
    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.id=?1 AND ci.product.id=?2")
    CartItems findCartItemsByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.cart.id=?1 AND ci.product.id=?2")
    void deleteCartItemsByProductIdAndCartId(Long cartId, Long productId);
    
}
