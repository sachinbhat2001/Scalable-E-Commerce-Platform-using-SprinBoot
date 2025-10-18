package com.example.cart_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class CartDto {
	
	@Data
	public static class CartItemDTO {
	    private Long productId;
	    private String productName;
	    private BigDecimal unitPrice;
	    
	    public Long getProductId() {
			return productId;
		}
		public void setProductId(Long productId) {
			this.productId = productId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
		private Integer quantity;
	    private BigDecimal totalPrice;
	}

	@Data
	public static class CartDTO {
	    private Long userId;
	    private List<CartItemDTO> items = new ArrayList<>();
	    private BigDecimal totalAmount;
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public List<CartItemDTO> getItems() {
			return items;
		}
		public void setItems(List<CartItemDTO> items) {
			this.items = items;
		}
		public BigDecimal getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}
		
	}
}
