package com.order.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
	
	
}
