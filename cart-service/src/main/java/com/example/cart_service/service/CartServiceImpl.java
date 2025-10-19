package com.example.cart_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cart_service.model.CartDto.CartDTO;
import com.example.cart_service.model.CartDto.CartItemDTO;
import com.example.cart_service.config.WebClientService;
import com.example.cart_service.model.*;
import com.example.cart_service.repository.CartRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
	@Autowired
	private CartRepository cartRepository;
	    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired 
    private WebClientService webClientService;
    
    private static final String orderUrl = "http://localhost:8084/api/order";

	@Override
	public CartDTO getCart(Long userId) {
		CartEntity cartEntity = cartRepository.findByUserIdWithItems(userId)
	            .orElseGet(() -> createNewCart(userId));

	    return convertToDTO(cartEntity);
//		CartEntity cartEntity = cartRepository.findByUserId(userId)
//			    .orElseGet(() -> createNewCart(userId));
//		
//		return convertToDTO(cartEntity);
				
	}
	private CartEntity createNewCart(Long userId) {
        CartEntity cart = new CartEntity();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }
	private CartDTO convertToDTO(CartEntity cartEntity) {
        CartDTO dto = new CartDTO();
        dto.setUserId(cartEntity.getUserId());
        dto.setItems(cartEntity.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()));
        dto.setTotalAmount(cartEntity.getTotalAmount());
        return dto;
    }
	private CartItemDTO convertItemToDTO(CartItemEntity item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
	@Override
	public CartDTO addItem(Long userId, CartItemDTO itemDTO) {
		CartEntity cart = cartRepository.findByUserIdWithItems(userId)
	            .orElseGet(() -> createNewCart(userId));
		
		Optional<CartItemEntity> existingItemOpt = cart.getItems().stream()
	            .filter(i -> i.getProductId().equals(itemDTO.getProductId()))
	            .findFirst();
		if (existingItemOpt.isPresent()) {
	        
	        CartItemEntity existingItem = existingItemOpt.get();
	        existingItem.setQuantity(existingItem.getQuantity() + itemDTO.getQuantity());
	        existingItem.setTotalPrice(existingItem.getUnitPrice()
	                .multiply(BigDecimal.valueOf(existingItem.getQuantity())));
	    } else {
	        
	        CartItemEntity newItem = new CartItemEntity();
	        newItem.setCart(cart);
	        newItem.setProductId(itemDTO.getProductId());
	        newItem.setProductName(itemDTO.getProductName());
	        newItem.setUnitPrice(itemDTO.getUnitPrice());
	        newItem.setQuantity(itemDTO.getQuantity());
	        newItem.setTotalPrice(itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
	        cart.getItems().add(newItem);
	    }
		
	
	    BigDecimal totalAmount = cart.getItems().stream()
	            .map(CartItemEntity::getTotalPrice)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	    cart.setTotalAmount(totalAmount);

	    // 5. Save cart
	    CartEntity savedCart = cartRepository.save(cart);
	    
	    return convertToDTO(savedCart);
	}
	@Override
	public CartDTO updateItemQuantity(Long userId, Long productId, Integer quantity) {
		CartEntity cart = cartRepository.findByUserIdWithItems(userId)
	            .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));

	    // 2. Find the item in the cart
	    CartItemEntity item = cart.getItems().stream()
	            .filter(i -> i.getProductId().equals(productId))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("Product not found in cart"));

	    // 3. Update quantity and total price
	    item.setQuantity(quantity);
	    item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

	    // 4. Update the cart's total amount
	    BigDecimal totalAmount = cart.getItems().stream()
	            .map(CartItemEntity::getTotalPrice)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	    cart.setTotalAmount(totalAmount);

	    // 5. Save the cart
	    CartEntity savedCart = cartRepository.save(cart);

	    // 6. Convert and return DTO
	    return convertToDTO(savedCart);
	}
	@Override
	public CartDTO removeItem(Long userId, Long productId) {
		CartEntity cart = cartRepository.findByUserIdWithItems(userId)
	            .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
		
		Optional<CartItemEntity> itemOpt = cart.getItems().stream()
		        .filter(i -> i.getProductId().equals(productId))
		        .findFirst();
		
		// 3. Remove the item if it exists
	    if(itemOpt.isPresent()) {
	        CartItemEntity item = itemOpt.get();
	        cart.getItems().remove(item); // removes from list
	        // JPA CascadeType.ALL + orphanRemoval = true will delete it from DB automatically
	    } else {
	        throw new RuntimeException("Product not found in cart: " + productId);
	    }
	    
	 // 4. Recalculate totalAmount
	    BigDecimal totalAmount = cart.getItems().stream()
	        .map(CartItemEntity::getTotalPrice)
	        .reduce(BigDecimal.ZERO, BigDecimal::add);
	    cart.setTotalAmount(totalAmount);
	    
	 // 5. Save cart
	    CartEntity savedCart = cartRepository.save(cart);
	    
	    // 6. Convert to DTO and return
	    return convertToDTO(savedCart);
		//return null;
	}
	@Override
	public void checkout(Long userId) {
		// 1. Fetch cart
	    CartEntity cart = cartRepository.findByUserIdWithItems(userId)
	        .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));

	    // 2. Check if cart is empty
	    if (cart.getItems().isEmpty()) {
	        throw new RuntimeException("Cannot checkout an empty cart");
	    }
	    
	    else {
	    	 
	    	OrderDTO orderDto=cartOrder(cart);
	    	webClientService.post(orderUrl, orderDto, String.class);
	    	
	    }

	    // 3. Send order details to a message queue or process order
	    // Example: rabbitTemplate.convertAndSend("order.exchange", "order.routingKey", cart);

	    // 4. Clear cart items after checkout
	    cart.getItems().clear();
	    cart.setTotalAmount(BigDecimal.ZERO);
	    
	    

	    // 5. Save cart
	    
	    cartRepository.save(cart);
	   
	    
	  
		
	}
	private OrderDTO cartOrder(CartEntity cart) {
		OrderDTO orderdto=new OrderDTO();
		orderdto.setUserId(cart.getUserId());
		orderdto.setStatus("PENDING");
		orderdto.setTotalAmount(cart.getTotalAmount());
		
		List<OrderItemDTO> itemDto = cart.getItems().stream().map(cartItem -> {
			OrderItemDTO dto = new OrderItemDTO();
			dto.setProductId(cartItem.getProductId());
			dto.setProductName(cartItem.getProductName());
			dto.setQuantity(cartItem.getQuantity());
			dto.setUnitPrice(cartItem.getUnitPrice());
			dto.setTotalPrice(cartItem.getTotalPrice());
			return dto;
	    }).collect(Collectors.toList());

		orderdto.setItems(itemDto);
	    
		return orderdto;
		
	}
    
    
}
