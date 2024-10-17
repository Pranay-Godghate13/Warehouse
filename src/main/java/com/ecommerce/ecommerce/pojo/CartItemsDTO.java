package com.ecommerce.ecommerce.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemsDTO {
    private Long cartItemsDTOId;
    private CartDTO carDTO;
    private ProductDTO productDTO;

    private Integer quantityDTO;
    private Double productPriceDTO;
    private Double discountDTO;
}
