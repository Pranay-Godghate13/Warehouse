package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.pojo.OrderDTO;

import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
            String pgStatus, String pgResponseMessage);
    
}