package com.ecommerce.ecommerce.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private List<ProductDTO> products=new ArrayList<>();
    private Double totalPrice=0.0;
}
