package com.payment.payment_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    private Long userId;
    
    private Long orderId;
    
    private Double amount;
    
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, etc.
    
    // Payment gateway details (simplified)
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}
