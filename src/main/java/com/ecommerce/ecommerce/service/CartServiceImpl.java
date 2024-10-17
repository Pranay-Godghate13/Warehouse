package com.ecommerce.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.exception.ApiException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.pojo.Cart;
import com.ecommerce.ecommerce.pojo.CartDTO;
import com.ecommerce.ecommerce.pojo.CartItems;
import com.ecommerce.ecommerce.pojo.Product;
import com.ecommerce.ecommerce.pojo.ProductDTO;
import com.ecommerce.ecommerce.repository.CartItemsRepository;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.repository.ProductRepository;
import com.ecommerce.ecommerce.util.AuthUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    ModelMapper modelMapper;
    
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart=createCart();

        Product product=productRepository.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));

        CartItems cartItem=cartItemsRepository.findCartItemsByProductIdAndCartId(cart.getCartId(),productId);
        if(cartItem!=null)
        {
            throw new ApiException("Product "+product.getProductName()+" already exists in the cart");
        }
        if(product.getQuantity()==0)
        {
            throw new ApiException(product.getProductName()+" is not available");
        }
        if(product.getQuantity()<0)
        {
            throw new ApiException("Please make an order of the "+product.getProductName()+" less than or equal to the quantity "+product.getQuantity()+".");
        }

        CartItems newCartItems=new CartItems();

        newCartItems.setProduct(product);
        newCartItems.setCart(cart);
        newCartItems.setQuantity(quantity);
        newCartItems.setDiscount(product.getDiscount());
        newCartItems.setProductPrice(product.getSpecialPrice());

        cartItemsRepository.save(newCartItems);

        product.setQuantity(product.getQuantity());
        
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

       // Long cartId=cart.getCartId();
        //Optional<CartItems> cartItems=cartItemsRepository.findById(cartId);
        List<CartItems> cartItems=cart.getCartItems();
          Stream<ProductDTO> productStream=cartItems.stream().map(item->{
                ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
                map.setQuantity(item.getQuantity());
                return map;
            });
            
            cartDTO.setProducts(productStream.toList());
            return cartDTO;
        
        
    
    }

    private Cart createCart() {
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null)
        {
            return userCart;
        }

        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        
        if(carts.size()==0)
        {
            throw new ApiException("No carts present");
        }
        List<CartDTO> cartDTOs=carts.stream().map(cart->{
            CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products=cart.getCartItems().stream().map(p->
                    modelMapper.map(p, ProductDTO.class)).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();
        return cartDTOs;
        /*List<CartDTO> response=carts.stream().map(cart->{
            CartDTO map=modelMapper.map(cart,CartDTO.class);
            List<CartItems> cartItems=cart.getCartItems();
            Stream<ProductDTO> productStream=cartItems.stream().map(p->{
                ProductDTO p=modelMapper.map(map.getProduct(),ProductDTO.class);
                p.setQuantity(map.getQuantity());
                return p;
            });
            
            map.setProducts(productStream.toList());
            return map;
        }).toList();
        return response;*/
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null)
        {
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(p->p.getProduct().setQuantity(p.getQuantity()));
        List<ProductDTO> products=cart.getCartItems().stream().map(p->
            modelMapper.map(p.getProduct(), ProductDTO.class)
        ).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId=authUtil.loggedInEmail();
        Cart userCart=cartRepository.findCartByEmail(emailId);
        Long cartId=userCart.getCartId();

        Cart cart=cartRepository.findById(cartId)
                    .orElseThrow(()->new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product=productRepository.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity()==0)
        {
            throw new ApiException(product.getProductName()+" id not available in stock");
        }
        
        if(product.getQuantity()<quantity)
        {
            throw new ApiException("Please make an order of the "+product.getProductName()+" less than or equal to the quantity "+product.getQuantity());
        }

        CartItems cartItems=cartItemsRepository.findCartItemsByProductIdAndCartId(cartId, productId);
        if(cartItems==null)
        {
            throw new ApiException("Product "+product.getProductName()+" is not available in the cart");
        }
        int newQuantity=cartItems.getQuantity()+quantity;
        if(newQuantity<0)
        {
            throw new ApiException("The resulting quantity cannot be negative!");
        }

        if(newQuantity==0)
        {
            deleteProductFromCart(cartId, productId);
        }
        else
        {
            cartItems.setProductPrice(product.getSpecialPrice());
            cartItems.setQuantity(cartItems.getQuantity()+quantity);
            cartItems.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice()+(cartItems.getProductPrice()*quantity));
            cartRepository.save(cart);    
        }
        
        CartItems updatedItem=cartItemsRepository.save(cartItems);
        if(updatedItem.getQuantity()==0)
        {
            cartItemsRepository.deleteById(updatedItem.getCartItemsId());
        }

        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
        List<CartItems> cartItem=cart.getCartItems();

        Stream<ProductDTO> productStream=cartItem.stream().map(item->{
            ProductDTO prod=modelMapper.map(item.getProduct(), ProductDTO.class);
            prod.setQuantity(item.getQuantity());
            return prod;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        
        Cart cart=cartRepository.findById(cartId)
                    .orElseThrow(()->new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItems cartItems=cartItemsRepository.findCartItemsByProductIdAndCartId(cartId, productId);
        if(cartItems==null)
        {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

       
        cart.setTotalPrice(cart.getTotalPrice()-(cartItems.getProductPrice()*cartItems.getQuantity()));


        cartItemsRepository.deleteCartItemsByProductIdAndCartId(cartId,productId);

        return "Product "+cartItems.getProduct().getProductName()+" removed from the cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));

         CartItems cartItems=cartItemsRepository.findCartItemsByProductIdAndCartId(cartId, productId);

         if(cartItems==null)
         {
            throw new ApiException("Product "+product.getProductName()+" not present");
         }

         double cartPrice=cart.getTotalPrice()-(cartItems.getProductPrice()*cartItems.getQuantity());

         cartItems.setProductPrice(product.getSpecialPrice());

         cart.setTotalPrice(cartPrice+(cartItems.getProductPrice()*cartItems.getQuantity()));

         cartItems=cartItemsRepository.save(cartItems);

    }
    
}
