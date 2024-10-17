package com.ecommerce.ecommerce.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.pojo.AddressDTO;
import com.ecommerce.ecommerce.pojo.User;
import com.ecommerce.ecommerce.service.AddressService;
import com.ecommerce.ecommerce.util.AuthUtil;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTO=addressService.getAllAddresses();
        return new ResponseEntity<List<AddressDTO>>(addressDTO, HttpStatus.FOUND);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getMethodName(@PathVariable Long addressId) {
        AddressDTO addressDTO=addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.FOUND);
    }
    
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser() {
        User user=authUtil.loggedInUser();
        List<AddressDTO> addressDTOs=addressService.getUserAddress(user);
        return new ResponseEntity<List<AddressDTO>>(addressDTOs, HttpStatus.FOUND);
    }
    
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> putMethodName(@PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress=addressService.updateAddress(addressId,addressDTO);
        return new ResponseEntity<AddressDTO>(updatedAddress, HttpStatus.CREATED);
    }
    
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user=authUtil.loggedInUser();
        AddressDTO addressDTOs=addressService.saveAddress(addressDTO,user);
        return new ResponseEntity<AddressDTO>(addressDTOs, HttpStatus.CREATED);

    }
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId)
    {
        String status=addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    

    
}
