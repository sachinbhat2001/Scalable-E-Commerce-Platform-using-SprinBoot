package com.example.cart_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cart_service.model.CartDto;
import com.example.cart_service.model.CartDto.CartDTO;
import com.example.cart_service.model.CartDto.CartItemDTO;
import com.example.cart_service.service.CartService;

@RestController
@RequestMapping("/api/carts")
public class CartController {

	
	@Autowired
    private CartService cartService;
	
	@GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }
	@PostMapping("/{userId}/items")
    public ResponseEntity<Object> addItem(@PathVariable Long userId, @RequestBody CartItemDTO itemDTO) {
        return ResponseEntity.ok(cartService.addItem(userId, itemDTO));
    }
	
	@PutMapping("/{userId}/items/{productId}")
	public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable Long userId, 
            @PathVariable Long productId, 
            @RequestParam Integer quantity){
		return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, quantity));
	}
	@DeleteMapping("/{userId}/items/{productId}")
	public ResponseEntity<CartDTO> removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }
	
	@PostMapping("/{userId}/checkout")
    public ResponseEntity<String> checkout(@PathVariable Long userId) {
        
		cartService.checkout(userId);
        return ResponseEntity.ok("Checkout initiated");
    }
	
}
