package com.order.order_service.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.order.order_service.model.OrderDTO;
import com.order.order_service.repository.OrderRepository;

public interface OrderService {
	
	OrderDTO processOrder(OrderDTO orderDto);
	List<OrderDTO> getOrdersByUserId(Long userId);
	//List<OrderDTO> orderDetails(Long userId);
	OrderDTO updateOrderStatus(Long orderId, String status);
}
