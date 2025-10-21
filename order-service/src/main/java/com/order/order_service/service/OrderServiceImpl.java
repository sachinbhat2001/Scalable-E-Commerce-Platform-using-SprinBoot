package com.order.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
//    @Override
//    public OrderDTO processOrder(OrderDTO orderDto) {
//        // Convert OrderDTO to OrderEntity and save to database
//        OrderEntity orderEntity = convertToEntity(orderDto);
//        orderEntity.setStatus("SUCCESSFUL"); // Set appropriate status
//        orderEntity = orderRepository.save(orderEntity);
//        
//        // Convert back to DTO and return
//        return convertToDTO(orderEntity);
//    }
    public OrderDTO processOrder(OrderDTO orderDto) {
        System.out.println("=== ORDER SERVICE: Processing order ===");
        System.out.println("Order received - User: " + orderDto.getUserId() + ", Items: " + orderDto.getItems().size());
        
        // Convert OrderDTO to OrderEntity and save to database
        OrderEntity orderEntity = convertToEntity(orderDto);
        orderEntity.setStatus("SUCCESSFUL");
        orderEntity = orderRepository.save(orderEntity);
        
        System.out.println("Order saved with ID: " + orderEntity.getId());
        System.out.println("=== ORDER SERVICE: Order processed successfully ===");
        
        return convertToDTO(orderEntity);
    }
    

//	@Override
//	public void processOrder(OrderDTO orderDto) {
//		
//		OrderEntity orderEntity=new OrderEntity();
//		//orderEntity.setId(orderDto.getId());
//		//orderEntity.setItems(orderDto.getItems());
//		orderEntity.setUserId(orderDto.getUserId());
//		orderEntity.setTotalAmount(orderDto.getTotalAmount());
//		orderEntity.setStatus(orderDto.getStatus());
//		orderEntity.setCreatedAt(LocalDateTime.now());
//		
//		List<OrderItemEntity> itemEntities = orderDto.getItems().stream().map(itemDTO -> {
//	        OrderItemEntity itemEntity = new OrderItemEntity();
//	        itemEntity.setProductId(itemDTO.getProductId());
//	        itemEntity.setProductName(itemDTO.getProductName());
//	        itemEntity.setQuantity(itemDTO.getQuantity());
//	        itemEntity.setUnitPrice(itemDTO.getUnitPrice());
//	        itemEntity.setTotalPrice(itemDTO.getTotalPrice());
//	        itemEntity.setOrder(orderEntity); // set parent reference
//	        return itemEntity;
//	    }).collect(Collectors.toList());
//
//	    orderEntity.setItems(itemEntities);
//	    
//	    orderRepository.save(orderEntity);
//
//	}

//	@Override
//	public OrderDTO orderDetails(Long userID) { // return list orderdto
//		List<OrderEntity> orderEntities = orderRepository.findOrdersWithItemsByUserId(userID);
//
//	    if (orderEntities.isEmpty()) {
//	        throw new RuntimeException("No orders found for userId: " + userID);
//	    }
//
//	    // Assuming you want the latest order or first order
//	   // OrderEntity orderEntity = orderEntities.get(0);
////list OrderDTO create
//	    for(iterate orderEntities and set orderdto{
//	    OrderDTO orderDto = new OrderDTO();
//	    orderDto.setId(orderEntity.getId());
//	    orderDto.setUserId(orderEntity.getUserId());
//	    orderDto.setTotalAmount(orderEntity.getTotalAmount());
//	    orderDto.setStatus(orderEntity.getStatus());
//	    orderDto.setCreatedAt(orderEntity.getCreatedAt());
//
//	    List<OrderItemDTO> items = orderEntity.getItems().stream().map(item -> {
//	        OrderItemDTO dto = new OrderItemDTO();
//	        dto.setProductId(item.getProductId());
//	        dto.setProductName(item.getProductName());
//	        dto.setQuantity(item.getQuantity());
//	        dto.setUnitPrice(item.getUnitPrice());
//	        dto.setTotalPrice(item.getTotalPrice());
//	        return dto;
//	    }).collect(Collectors.toList());
//
//	    orderDto.setItems(items);
//	    return orderDto; 
//	   }
//	}
    //----------------------
	//ORDERDETAILS METHOD
    //----------------------
//	@Override
//	public List<OrderDTO> orderDetails(Long userID) {
//		
//	    List<OrderEntity> orderEntities = orderRepository.findOrdersWithItemsByUserId(userID);
//
//	    if (orderEntities.isEmpty()) {
//	        throw new RuntimeException("No orders found for userId: " + userID);
//	    }
//
//	    // Create List of OrderDTO from all order entities
//	    List<OrderDTO> orderDtos = new ArrayList<>();
//	    
//	    for(OrderEntity orderEntity : orderEntities) {
//	        OrderDTO orderDto = new OrderDTO();
//	        orderDto.setId(orderEntity.getId());
//	        orderDto.setUserId(orderEntity.getUserId());
//	        orderDto.setTotalAmount(orderEntity.getTotalAmount());
//	        orderDto.setStatus(orderEntity.getStatus());
//	        orderDto.setCreatedAt(orderEntity.getCreatedAt());
//
//	        List<OrderItemDTO> items = orderEntity.getItems().stream().map(item -> {
//	            OrderItemDTO dto = new OrderItemDTO();
//	            dto.setProductId(item.getProductId());
//	            dto.setProductName(item.getProductName());
//	            dto.setQuantity(item.getQuantity());
//	            dto.setUnitPrice(item.getUnitPrice());
//	            dto.setTotalPrice(item.getTotalPrice());
//	            return dto;
//	        }).collect(Collectors.toList());
//
//	        orderDto.setItems(items);
//	        orderDtos.add(orderDto); // Add each order to the list
//	    }
//	    
//	    return orderDtos; // Return List<OrderDTO>
//	}
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
        orderDto.setId(updatedOrder.getId());
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
	public List<OrderDTO> getOrdersByUserId(Long userId) {
	    System.out.println("=== ORDER SERVICE: Getting orders for user " + userId + " ===");
	    
	    // Method 1: Simple repository method
	    List<OrderEntity> orders1 = orderRepository.findByUserId(userId);
	    System.out.println("1. findByUserId found: " + orders1.size() + " orders");
	    
	    // Method 2: Complex method with JOIN FETCH
	    List<OrderEntity> orders2 = orderRepository.findOrdersWithItemsByUserId(userId);
	    System.out.println("2. findOrdersWithItemsByUserId found: " + orders2.size() + " orders");


	    
	    
	    // Method 3: Get ALL orders to see what's in the database
	    List<OrderEntity> allOrders = orderRepository.findAll();
	    System.out.println("3. Total orders in database: " + allOrders.size());
	    
	    // Print details of all orders
	    for (OrderEntity order : allOrders) {
	        System.out.println("   Order ID: " + order.getId() + 
	                          ", User ID: " + order.getUserId() + 
	                          ", Status: " + order.getStatus());
	    }
	    
	    // Method 4: Check if our specific order exists
	    Optional<OrderEntity> specificOrder = orderRepository.findById(34L);
	    System.out.println("4. Order ID 34 exists: " + specificOrder.isPresent());
	    if (specificOrder.isPresent()) {
	        System.out.println("   Order 34 User ID: " + specificOrder.get().getUserId());
	    }
	    
	    // Use the method that works
	    List<OrderEntity> orderEntities = orderRepository.findOrdersWithItemsByUserIdOrderByCreatedAtDesc(userId);
	    List<OrderDTO> dtos = orderEntities.stream()
	            .map(this::convertToDTO)
	            .collect(Collectors.toList());
	    
	    System.out.println("Returning " + dtos.size() + " orders to frontend");
	    System.out.println("=== ORDER SERVICE: Order retrieval completed ===");
	    
	    return dtos;
	}
//	public List<OrderDTO> getOrdersByUserId(Long userId) {
//        System.out.println("=== ORDER SERVICE: Getting orders for user " + userId + " ===");
//        
//        // Try different methods
//        List<OrderEntity> orders1 = orderRepository.findByUserId(userId);
//        System.out.println("findByUserId found: " + orders1.size() + " orders");
//        
//        List<OrderEntity> orders2 = orderRepository.findOrdersWithItemsByUserId(userId);
//        System.out.println("findOrdersWithItemsByUserId found: " + orders2.size() + " orders");
//        
//        List<OrderDTO> dtos = orders2.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//        
//        System.out.println("Returning " + dtos.size() + " orders to frontend");
//        return dtos;
//    }
	
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
	private OrderEntity convertToEntity(OrderDTO dto) {
        OrderEntity entity = new OrderEntity();
        entity.setUserId(dto.getUserId());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setStatus(dto.getStatus());
        entity.setShippingAddress(dto.getShippingAddress());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setCreatedAt(java.time.LocalDateTime.now());
        
        // Convert order items
        List<OrderItemEntity> itemEntities = dto.getItems().stream()
                .map(itemDto -> {
                    OrderItemEntity itemEntity = new OrderItemEntity();
                    itemEntity.setProductId(itemDto.getProductId());
                    itemEntity.setProductName(itemDto.getProductName());
                    itemEntity.setQuantity(itemDto.getQuantity());
                    itemEntity.setUnitPrice(itemDto.getUnitPrice());
                    itemEntity.setTotalPrice(itemDto.getTotalPrice());
                    itemEntity.setOrder(entity);
                    return itemEntity;
                })
                .collect(Collectors.toList());
        
        entity.setItems(itemEntities);
        return entity;
    }
	private OrderDTO convertToDTO(OrderEntity entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setStatus(entity.getStatus());
        dto.setShippingAddress(entity.getShippingAddress());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setCreatedAt(entity.getCreatedAt());
        
        // Convert order items
        List<OrderItemDTO> itemDTOs = entity.getItems().stream()
                .map(itemEntity -> {
                    OrderItemDTO itemDto = new OrderItemDTO();
                    itemDto.setProductId(itemEntity.getProductId());
                    itemDto.setProductName(itemEntity.getProductName());
                    itemDto.setQuantity(itemEntity.getQuantity());
                    itemDto.setUnitPrice(itemEntity.getUnitPrice());
                    itemDto.setTotalPrice(itemEntity.getTotalPrice());
                    return itemDto;
                })
                .collect(Collectors.toList());
        
        dto.setItems(itemDTOs);
        return dto;
    }

	
}
	
	