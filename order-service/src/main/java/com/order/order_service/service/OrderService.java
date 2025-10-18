package com.order.order_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.order.order_service.model.OrderDTO;
import com.order.order_service.repository.OrderRepository;

public interface OrderService {
	
	void processOrder(OrderDTO orderDto);
}
