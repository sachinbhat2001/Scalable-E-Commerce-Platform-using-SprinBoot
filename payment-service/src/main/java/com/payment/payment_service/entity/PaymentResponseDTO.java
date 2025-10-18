package com.payment.payment_service.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private Double amount;
    private String status;
    private String transactionId;
    private String message;
    private LocalDateTime paymentDate;
}