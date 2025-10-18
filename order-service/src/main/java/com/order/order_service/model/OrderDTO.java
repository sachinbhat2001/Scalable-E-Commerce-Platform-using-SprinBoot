package com.order.order_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;//ArrayList;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class OrderDTO {
//    private Long id;
    private Long userId;
    private List<OrderItemDTO> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
	
}



