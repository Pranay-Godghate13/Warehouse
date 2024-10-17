package com.ecommerce.ecommerce.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3,message="Product name should contain 3 characters")
    private String productName;
    @NotBlank(message = "Description cannot be blank")
    @Size(min = 6,message="Description should contain 6 characters")
    private String description;
    private String image;
    @Min(value=1,message="Should be atleast 1")
    private Integer quantity;
    @Min(value=1,message="Should be atleast 1")
    private double price;
    private double discount;
    private double specialPrice;
    
}
