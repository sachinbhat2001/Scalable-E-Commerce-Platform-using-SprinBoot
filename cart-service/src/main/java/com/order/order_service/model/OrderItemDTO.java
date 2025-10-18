package com.order.order_service.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OrderItemDTO {
	
	
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
   
}