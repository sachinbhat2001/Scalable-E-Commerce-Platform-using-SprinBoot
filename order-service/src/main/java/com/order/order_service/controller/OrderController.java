package com.order.order_service.controller;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

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
import com.order.order_service.model.OrderEntity;
import com.order.order_service.model.OrderItemEntity;
import com.order.order_service.model.OrderStatusUpdateRequestDTO;
import com.order.order_service.repository.OrderRepository;
import com.order.order_service.service.OrderService;


@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	@Autowired
	public OrderService orderService;


	@Autowired
	public OrderRepository orderRepository;
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
	    long count = orderRepository.count();
	    return ResponseEntity.ok("Total orders in database: " + count);
	}
	
	@PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDto){
        OrderDTO createdOrder = orderService.processOrder(orderDto);
        return ResponseEntity.ok(createdOrder);
    }
	@GetMapping("/debug/all")
	public ResponseEntity<List<OrderDTO>> debugAllOrders() {
	    System.out.println("=== DEBUG: Getting ALL orders ===");
	    
	    // Get all orders from the database
	    List<OrderEntity> allOrders = orderRepository.findAll();
	    System.out.println("Total orders in database: " + allOrders.size());
	    
	    // Print each order's details
	    for (OrderEntity order : allOrders) {
	        System.out.println("Order ID: " + order.getId() + 
	                          ", User ID: " + order.getUserId() + 
	                          ", Status: " + order.getStatus() +
	                          ", Total: " + order.getTotalAmount());
	    }
	    
	    // Use the service to convert to DTOs
	    List<OrderDTO> dtos = allOrders.stream()
	            .map(order -> {
	                // Create a simple conversion here or call service method
	                OrderDTO dto = new OrderDTO();
	                dto.setId(order.getId());
	                dto.setUserId(order.getUserId());
	                dto.setTotalAmount(order.getTotalAmount());
	                dto.setStatus(order.getStatus());
	                dto.setShippingAddress(order.getShippingAddress());
	                dto.setPaymentMethod(order.getPaymentMethod());
	                dto.setCreatedAt(order.getCreatedAt());
	                return dto;
	            })
	            .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(dtos);
	}

	@GetMapping("/debug/user/{userId}")
	public ResponseEntity<List<OrderDTO>> debugUserOrders(@PathVariable Long userId) {
	    System.out.println("=== DEBUG: Getting orders for user: " + userId + " ===");
	    
	    // Use the existing service method instead
	    List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
	    System.out.println("Service returned: " + orders.size() + " orders");
	    
	    // Also check repository directly for debugging
	    List<OrderEntity> repoOrders = orderRepository.findByUserId(userId);
	    System.out.println("Repository directly found: " + repoOrders.size() + " orders");
	    
	    List<OrderEntity> allOrders = orderRepository.findAll();
	    System.out.println("All orders user IDs: " + 
	        allOrders.stream().map(OrderEntity::getUserId).collect(Collectors.toList()));
	    
	    return ResponseEntity.ok(orders);
	}
	
	//This api is called from Cart microservcie when chekout btn is clicked in UI
//	@PostMapping
//	public String listOfId(@RequestBody OrderDTO orderDto){
//		orderService.processOrder(orderDto);
//		return "Order is Ready to be Placed";
//	}
	//This api should be called as soon as I land on order tab in UI
//	@GetMapping("/orderDetails/{userID}")
//	public ResponseEntity<List<OrderDTO>> orderDetails(@PathVariable Long userID){
//		
//		List<OrderDTO> dto=new ArrayList<>();
//		//OrderDTO dto = orderService.orderDetails(userID);
//		return ResponseEntity.ok(dto);
//	}
	@GetMapping("/orderDetails/{userID}")
	public ResponseEntity<List<OrderDTO>> orderDetails(@PathVariable Long userID){
	    System.out.println("=== ORDER CONTROLLER: Getting order details for user " + userID + " ===");
	    
	    List<OrderDTO> orders = orderService.getOrdersByUserId(userID);
	    System.out.println("Returning " + orders.size() + " orders to frontend");
	    
	    return ResponseEntity.ok(orders);
	}
	@PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody OrderStatusUpdateRequestDTO statusRequest) {
        
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, statusRequest.getStatus());
        return ResponseEntity.ok("Order status updated to: " + updatedOrder.getStatus());
    }
	
	 @PutMapping("/{orderId}/ready")
	    public ResponseEntity<OrderDTO> markOrderReady(@PathVariable Long orderId) {
	        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, "READY");
	        return ResponseEntity.ok(updatedOrder);
	    }
	 
	 //##########################
	 //debug endpoint
	 //##########################
	 @GetMapping("/debug/items/{orderId}")
	 public ResponseEntity<Map<String, Object>> debugOrderItems(@PathVariable Long orderId) {
	     System.out.println("=== DEBUG ORDER ITEMS FOR ORDER " + orderId + " ===");
	     
	     Map<String, Object> response = new HashMap<>();
	     
	     Optional<OrderEntity> order = orderRepository.findById(orderId);
	     response.put("orderExists", order.isPresent());
	     
	     if (order.isPresent()) {
	         OrderEntity orderEntity = order.get();
	         response.put("orderId", orderEntity.getId());
	         response.put("userId", orderEntity.getUserId());
	         response.put("itemsCount", orderEntity.getItems().size());
	         
	         // Convert items to simple map for debugging
	         List<Map<String, Object>> itemsDebug = orderEntity.getItems().stream()
	             .map(item -> {
	                 Map<String, Object> itemMap = new HashMap<>();
	                 itemMap.put("productId", item.getProductId());
	                 itemMap.put("productName", item.getProductName());
	                 itemMap.put("quantity", item.getQuantity());
	                 itemMap.put("unitPrice", item.getUnitPrice());
	                 itemMap.put("totalPrice", item.getTotalPrice());
	                 return itemMap;
	             })
	             .collect(Collectors.toList());
	         
	         response.put("items", itemsDebug);
	         
	         System.out.println("Order " + orderId + " has " + orderEntity.getItems().size() + " items");
	         for (OrderItemEntity item : orderEntity.getItems()) {
	             System.out.println("  - " + item.getProductName() + " x" + item.getQuantity());
	         }
	     }
	     
	     return ResponseEntity.ok(response);
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