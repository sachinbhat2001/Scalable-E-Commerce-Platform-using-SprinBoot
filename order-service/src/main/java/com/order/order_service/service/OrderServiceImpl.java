package com.order.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.*;

import org.hibernate.query.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import com.order.order_service.model.OrderDTO;
import com.order.order_service.model.OrderEntity;
import com.order.order_service.model.OrderItemDTO;
import com.order.order_service.model.OrderItemEntity;
import com.order.order_service.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private OrderDTO orderDto;

	@Override
	public void processOrder(OrderDTO orderDto) {
		
		OrderEntity orderEntity=new OrderEntity();
		//orderEntity.setId(orderDto.getId());
		//orderEntity.setItems(orderDto.getItems());
		orderEntity.setUserId(orderDto.getUserId());
		orderEntity.setTotalAmount(orderDto.getTotalAmount());
		orderEntity.setStatus(orderDto.getStatus());
		orderEntity.setCreatedAt(LocalDateTime.now());
		
		List<OrderItemEntity> itemEntities = orderDto.getItems().stream().map(itemDTO -> {
	        OrderItemEntity itemEntity = new OrderItemEntity();
	        itemEntity.setProductId(itemDTO.getProductId());
	        itemEntity.setProductName(itemDTO.getProductName());
	        itemEntity.setQuantity(itemDTO.getQuantity());
	        itemEntity.setUnitPrice(itemDTO.getUnitPrice());
	        itemEntity.setTotalPrice(itemDTO.getTotalPrice());
	        itemEntity.setOrder(orderEntity); // set parent reference
	        return itemEntity;
	    }).collect(Collectors.toList());

	    orderEntity.setItems(itemEntities);
	    
	    orderRepository.save(orderEntity);

	}

	@Override
	public OrderDTO orderDetails(Long userID) {
		List<OrderEntity> orderEntities = orderRepository.findOrdersWithItemsByUserId(userID);

	    if (orderEntities.isEmpty()) {
	        throw new RuntimeException("No orders found for userId: " + userID);
	    }

	    // Assuming you want the latest order or first order
	    OrderEntity orderEntity = orderEntities.get(0);

	    // Convert OrderEntity -> OrderDTO
	    OrderDTO orderDto = new OrderDTO();
	    orderDto.setUserId(orderEntity.getUserId());
	    orderDto.setTotalAmount(orderEntity.getTotalAmount());
	    orderDto.setStatus(orderEntity.getStatus());
	    orderDto.setCreatedAt(orderEntity.getCreatedAt());

	    List<OrderItemDTO> items = orderEntity.getItems().stream().map(item -> {
	        OrderItemDTO dto = new OrderItemDTO();
	        dto.setProductId(item.getProductId());
	        dto.setProductName(item.getProductName());
	        dto.setQuantity(item.getQuantity());
	        dto.setUnitPrice(item.getUnitPrice());
	        dto.setTotalPrice(item.getTotalPrice());
	        return dto;
	    }).collect(Collectors.toList());

	    orderDto.setItems(items);
	    return orderDto;
	}
	@Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Update status
        order.setStatus(status);

        // Save updated entity
        OrderEntity updatedOrder = orderRepository.save(order);

        // Convert Entity → DTO to return
        OrderDTO orderDto = new OrderDTO();
        orderDto.setUserId(updatedOrder.getUserId());
        orderDto.setTotalAmount(updatedOrder.getTotalAmount());
        orderDto.setStatus(updatedOrder.getStatus());
        orderDto.setCreatedAt(updatedOrder.getCreatedAt());

        // Convert Order Items
        List<OrderItemDTO> items = updatedOrder.getItems().stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductId(item.getProductId());
            itemDto.setProductName(item.getProductName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setTotalPrice(item.getTotalPrice());
            return itemDto;
        }).collect(Collectors.toList());

        orderDto.setItems(items);

        return orderDto;
    }
	
//	@RabbitListener(queues = "order.queue")
//    public void handleOrderCreation(OrderEvent orderEvent) {
//		
//        if ("ORDER_CREATED".equals(orderEvent.getEventType())) {
//            createOrder(orderEvent);
//        }
//    }
//
////	private void createOrder(OrderEvent orderEvent) {
////		// TODO Auto-generated method stub
////		
////	}
//	private void createOrder(OrderEvent eventData) {
//		
//		Long userId = Long.valueOf(eventData.get("userId").toString());
//	    BigDecimal amount = new BigDecimal(eventData.get("amount").toString());
//        
//        // Create order from cart items
//        OrderEntity orderEntity = new OrderEntity();
//        orderEntity.setUserId(userId);
//        orderEntity.setTotalAmount(amount);
//        orderEntity.setStatus("PENDING");
//        orderEntity.setCreatedAt(LocalDateTime.now());
//        
//        // Convert cart items to order items
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) eventData.get("cartItems");
//        List<OrderItemEntity> orderItems = cartItems.stream().map(item -> {
//            OrderItemEntity orderItem = new OrderItemEntity();
//            orderItem.setOrder(orderEntity); // set parent order
//            orderItem.setProductId(Long.valueOf(item.get("productId").toString()));
//            orderItem.setProductName(item.get("productName").toString());
//            orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
//            orderItem.setUnitPrice(new BigDecimal(item.get("unitPrice").toString()));
//            orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
//            return orderItem;
//        }).collect(Collectors.toList());
//        
//        orderEntity.setItems(orderItems);
//        OrderEntity savedOrder = orderRepository.save(orderEntity);
//        
//        // Publish ORDER_CREATED event with order ID
//        OrderEvent orderCreatedEvent = new OrderEvent("ORDER_CREATED", savedOrder.getId(), userId, amount);
//        rabbitTemplate.convertAndSend("order.exchange", "order.created", orderCreatedEvent)
//
//
//}

	
}
	
	