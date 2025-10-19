package com.example.cart_service.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
