package com.payment.payment_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    
    public void sendPaymentConfirmation(Long userId, Long orderId) {
        log.info("✉️ MOCK EMAIL: Sending payment confirmation to user {} for order {}", userId, orderId);
        log.info("📧 Email would contain: Order {}, Transaction details, Receipt", orderId);
        log.info("📧 Email would be sent to: user{}@example.com", userId);
        // In real implementation, this would call SendGrid, AWS SES, etc.
    }
    
    public void sendPaymentFailureNotification(Long userId, Long orderId, String reason) {
        log.info("✉️ MOCK EMAIL: Sending payment failure notification to user {} for order {}", userId, orderId);
        log.info("📧 Failure reason: {}", reason);
        log.info("📧 Email would suggest retrying payment");
    }
}