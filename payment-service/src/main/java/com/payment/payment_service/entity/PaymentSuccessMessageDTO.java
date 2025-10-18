package com.payment.payment_service.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessMessageDTO {
    private Long userId;
    private Long orderId;
    private Double amount;
    private String transactionId;
    private LocalDateTime paymentTime;
}