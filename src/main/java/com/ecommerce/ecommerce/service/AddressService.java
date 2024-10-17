package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.pojo.AddressDTO;
import com.ecommerce.ecommerce.pojo.User;

import jakarta.validation.Valid;

public interface AddressService {

    AddressDTO saveAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddress(User user);

    AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddress(Long addressId);

    
} 
