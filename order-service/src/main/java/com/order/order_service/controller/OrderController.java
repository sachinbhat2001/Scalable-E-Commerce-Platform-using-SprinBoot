package com.order.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.order.order_service.model.OrderDTO;
import com.order.order_service.service.OrderService;


@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	@Autowired
	public OrderService orderService;
	
	@PostMapping
	public String listOfId(@RequestBody OrderDTO orderDto){
		orderService.processOrder(orderDto);
		return "Order is Ready to be Placed";
	}
	@GetMapping("/orderDetails/{userID}")
	public ResponseEntity<OrderDTO> orderDetails(@PathVariable Long userID){
		OrderDTO dto = orderService.orderDetails(userID);
		return ResponseEntity.ok(dto);
	}
	
	 @PutMapping("/{orderId}/ready")
	    public ResponseEntity<OrderDTO> markOrderReady(@PathVariable Long orderId) {
	        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, "READY");
	        return ResponseEntity.ok(updatedOrder);
	    }
}
/*
 
 ecommerce-platform/
├── docker-compose.yml
├── api-gateway/
├── service-discovery/
├── user-service/
├── product-service/
├── cart-service/
├── order-service/
├── payment-service/
├── notification-service/
└── shared/
*/