package com.example.cart_service.service;

import com.example.cart_service.model.CartDto;
import com.example.cart_service.model.CartDto.CartDTO;
import com.example.cart_service.model.CartDto.CartItemDTO;

public interface CartService {

	
	public CartDTO getCart(Long userId);

	public CartDTO addItem(Long userId, CartItemDTO itemDTO);

	public CartDTO updateItemQuantity(Long userId, Long productId, Integer quantity);

	public CartDTO removeItem(Long userId, Long productId);

	public void checkout(Long userId);
	
	
	
}
