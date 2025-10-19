package com.payment.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.payment.payment_service.entity.OrderStatusUpdateRequestDTO;

@FeignClient(name = "order-service", url = "http://localhost:8084") 
public interface OrderServiceClient {
    
    @PutMapping("/api/orders/status")
    void updateOrderStatus(@RequestBody OrderStatusUpdateRequestDTO request);
}