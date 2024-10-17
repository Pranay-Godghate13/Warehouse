package com.ecommerce.ecommerce.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.pojo.OrderDTO;
import com.ecommerce.ecommerce.pojo.OrderRequestDTO;
import com.ecommerce.ecommerce.service.OrderService;
import com.ecommerce.ecommerce.util.AuthUtil;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    AuthUtil authUtil;
  
    @Autowired
    private OrderService orderService;
    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> postMethodName(@PathVariable String paymentMethod,@RequestBody OrderRequestDTO orderRequestDTO) {
        
        String emailId=authUtil.loggedInEmail();
        OrderDTO order=orderService.placeOrder(
        
            emailId,
            orderRequestDTO.getAddressId(),
            paymentMethod,
            orderRequestDTO.getPgName(),
            orderRequestDTO.getPgPaymentId(),
            orderRequestDTO.getPgStatus(),
            orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    
}