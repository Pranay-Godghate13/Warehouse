package com.ecommerce.ecommerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.exception.ApiException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.pojo.Address;
import com.ecommerce.ecommerce.pojo.Cart;
import com.ecommerce.ecommerce.pojo.CartItems;
import com.ecommerce.ecommerce.pojo.Order;
import com.ecommerce.ecommerce.pojo.OrderDTO;
import com.ecommerce.ecommerce.pojo.OrderItem;
import com.ecommerce.ecommerce.pojo.OrderItemDTO;
import com.ecommerce.ecommerce.pojo.Payment;
import com.ecommerce.ecommerce.pojo.Product;
import com.ecommerce.ecommerce.repository.AddressRepository;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.repository.OrderItemRepository;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.repository.PaymentRepository;
import com.ecommerce.ecommerce.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    CartRepository  cartRepository;
    
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
            String pgStatus, String pgResponseMessage) {

            Cart cart=cartRepository.findCartByEmail(emailId);
            if(cart==null)
            {
                throw new ResourceNotFoundException("Cart", "email", emailId);
            }
            Address address=addressRepository.findById(addressId)
                        .orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));
            
            
            
            
            
            Order order=new Order();
            order.setEmail(emailId);
            order.setOrderDate(LocalDate.now());
            order.setTotalAmount(cart.getTotalPrice());
            order.setOrderStatus("Order Accepted !");
            order.setAddress(address);
           
            Payment payment=new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
            payment.setOrder(order);
            
            payment=paymentRepository.save(payment);
            order.setPayment(payment);

            Order savedOrder=orderRepository.save(order);

            List<CartItems> cartItems=cart.getCartItems();
            if(cartItems.isEmpty())
            {
                throw new ApiException("Cart is empty");
            }

            List<OrderItem> orderItems=new ArrayList<>();
            for(CartItems cartItem:cartItems)
            {
                OrderItem orderItem=new OrderItem();
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setDiscount(cartItem.getDiscount());
                orderItem.setOrderProductPrice(cartItem.getProductPrice());
                orderItem.setOrder(savedOrder);
                orderItems.add(orderItem);
            }
            orderItems=orderItemRepository.saveAll(orderItems);

            cart.getCartItems().forEach(item->{
                int quantity=item.getQuantity();
                Product product=item.getProduct();

                product.setQuantity(product.getQuantity()-quantity);
                productRepository.save(product);
                cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
            });
            
            OrderDTO orderDTO=modelMapper.map(savedOrder, OrderDTO.class);
            orderItems.forEach(item->orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));
            orderDTO.setAddressId(addressId);
            return orderDTO;
    }
    
}
