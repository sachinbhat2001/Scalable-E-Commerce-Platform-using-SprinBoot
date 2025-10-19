package com.example.cart_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



import lombok.Data;

@Data
public class OrderDTO {
	
//  private Long id;
  private Long userId;
  private List<OrderItemDTO> items = new ArrayList<OrderItemDTO>();
  private BigDecimal totalAmount;
  private String status;
  private LocalDateTime createdAt;

}
