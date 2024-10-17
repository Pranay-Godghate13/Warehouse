package com.ecommerce.ecommerce.service;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.exception.ApiException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.pojo.Address;
import com.ecommerce.ecommerce.pojo.AddressDTO;
import com.ecommerce.ecommerce.pojo.User;
import com.ecommerce.ecommerce.repository.AddressRepository;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.util.AuthUtil;

import jakarta.validation.Valid;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AddressDTO saveAddress(AddressDTO addressDTO,User user) {
       Address address=modelMapper.map(addressDTO,Address.class);
       List<Address> addressList=user.getAddress();
       addressList.add(address);
       user.setAddress(addressList);
       address.setUser(user);
       Address savedAddress=addressRepository.save(address);
       return modelMapper.map(savedAddress, AddressDTO.class);
        /*String emailId=authUtil.loggedInEmail();
        Long userId=userRepository.findIdByEmail(emailId);
        Address address=addressRepository.findByUserId(userId);
        if(address!=null)
        {
            throw new ApiException("Address already saved!!");
        }
        Address newAddress=modelMapper.map(addressDTO,Address.class);
        Address savedAddress=addressRepository.save(newAddress);
        AddressDTO response=modelMapper.map(savedAddress, AddressDTO.class);
        return response;*/
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> allAddress=addressRepository.findAll();
        if(allAddress.size()==0)
        {
            throw new ApiException("No addresses present");
        }
        List<AddressDTO> allAddressDTOs=allAddress.stream().map(address->
            modelMapper.map(address, AddressDTO.class)
        ).toList();
        return allAddressDTOs;

    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address addressOpt=addressRepository.findById(addressId)
                    .orElseThrow(()->new ResourceNotFoundException("Address", "AddressId", addressId));

        AddressDTO addressDTO=modelMapper.map(addressOpt, AddressDTO.class);
        return addressDTO;

    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {
        List<Address> address=user.getAddress();
        if(address.size()==0)
        throw new ApiException("Add some addresses");
        List<AddressDTO> addressDTOs=address.stream().map(item->
            modelMapper.map(item, AddressDTO.class)
        ).toList();
        return addressDTOs;
    }

    @Override
    public AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO) {
        Optional<Address> address=addressRepository.findById(addressId);
        Address savedAddress=address.orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));

        savedAddress.setAddressId(addressDTO.getAddressId());
        savedAddress.setBuildingName(addressDTO.getBuildingName());
        savedAddress.setCity(addressDTO.getCity());
        savedAddress.setCountry(addressDTO.getCountry());
        savedAddress.setPincode(addressDTO.getPincode());
        savedAddress.setState(addressDTO.getState());
        savedAddress.setStreet(addressDTO.getStreet());
        Address updatedAddress=addressRepository.save(savedAddress);
        User user=savedAddress.getUser();
        user.getAddress().removeIf(addres->addres.getAddressId().equals(addressId));
        user.getAddress().add(updatedAddress);
        userRepository.save(user);
        AddressDTO updateAddressDTO=modelMapper.map(updatedAddress,AddressDTO.class);
        return updateAddressDTO;
    }

    @Override
    public String deleteAddress(Long addressId) {
        Optional<Address> address=addressRepository.findById(addressId);
        Address savedAddress=address.orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));
        User user=savedAddress.getUser();
        user.getAddress().removeIf(addres->addres.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(savedAddress);
        return "Address is removed of address Id: "+addressId;
    }

    


    
}
