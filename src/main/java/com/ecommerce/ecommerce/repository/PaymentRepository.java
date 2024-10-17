package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.pojo.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long>{
    
}
