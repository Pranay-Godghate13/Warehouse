package com.ecommerce.ecommerce.pojo;

import org.modelmapper.internal.util.Stack;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min =5,message = "Street name must be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min =5,message = "Building name must be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min =1,message = "City name must be atleast 5 characters")
    private String city;

    @NotBlank
    @Size(min =2,message = "State name must be atleast 2 characters")
    private String state;

    @NotBlank
    @Size(min =6,message = "Country name must be atleast 6 characters")
    private String country;

    @NotBlank
    @Size(min =5,message = "Pincode name must be atleast 5 characters")
    private String pincode;

    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street,String buildingName,String city,String state,String pincode,String country)
    {
        this.street=street;
        this.buildingName=buildingName;
        this.city=city;
        this.state=state;
        this.pincode=pincode;
        this.country=country;
    }

   

}
