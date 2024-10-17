package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.ecommerce.pojo.Address;
import com.ecommerce.ecommerce.pojo.User;

public interface AddressRepository extends JpaRepository<Address,Long>{
    @Query("SELECT a FROM Address a WHERE a.user.userId =?1")
    Address findByUserId( Long userId);
    
}
