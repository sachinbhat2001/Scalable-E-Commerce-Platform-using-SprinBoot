package com.payment.payment_service.service;

import com.payment.payment_service.client.OrderServiceClient;
import com.payment.payment_service.config.WebClientService;
import com.payment.payment_service.entity.*;
import com.payment.payment_service.entity.PaymentEntity;
import com.payment.payment_service.entity.PaymentRequestDTO;
import com.payment.payment_service.entity.PaymentResponseDTO;
import com.payment.payment_service.rabbitmq.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceHelperMethods {

    private final RabbitMQProducer rabbitMQProducer;
    private final WebClientService webClientService;
    private static final String ORDER_SERVICE_BASE_URL = "http://localhost:8084/api/order";

    // Transaction ID generation
    public String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    // Payment validation
    public boolean validatePayment(PaymentRequestDTO paymentRequest) {
        log.info("VALIDATION: Starting payment validation for order: {}", paymentRequest.getOrderId());
        
        // Basic validation
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
            log.warn("Invalid amount: {}", paymentRequest.getAmount());
            return false;
        }
        
        // Validate card details if payment method is card
        if (paymentRequest.getPaymentMethod() != null && 
            paymentRequest.getPaymentMethod().toUpperCase().contains("CARD")) {
            
            // More lenient validation for testing
            if (paymentRequest.getCardNumber() == null || 
                paymentRequest.getCardNumber().replaceAll("\\s", "").length() < 13) {
                log.warn("Invalid card number: {}", paymentRequest.getCardNumber());
                return false;
            }
            
            // Accept common test card numbers
            String cleanCardNumber = paymentRequest.getCardNumber().replaceAll("\\s", "");
            if (cleanCardNumber.matches("^(4111|5555|3782).*")) {
                log.info("Test card number accepted: {}", cleanCardNumber);
                return true;
            }
        }
        
        // Simulate payment gateway response (90% success rate for demo)
        boolean isSuccessful = Math.random() > 0.1;
        log.info("Payment validation result: {}", isSuccessful ? "SUCCESS" : "FAILED");
        return isSuccessful;
    }
//    public boolean validatePayment(PaymentRequestDTO paymentRequest) {
//    	log.info("VALIDATION: Starting payment validation for order: {}", paymentRequest.getOrderId());
//        // Basic validation
//        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
//            log.warn("Invalid amount: {}", paymentRequest.getAmount());
//            return false;
//        }
//        
//        // Validate card details if payment method is card
//        if (paymentRequest.getPaymentMethod() != null && 
//            paymentRequest.getPaymentMethod().toUpperCase().contains("CARD")) {
//            if (paymentRequest.getCardNumber() == null || paymentRequest.getCardNumber().length() < 16) {
//                log.warn("Invalid card number");
//                return false;
//            }
//        }
//        
//        // Simulate payment gateway response (90% success rate for demo)
//        boolean isSuccessful = Math.random() > 0.1;
//        log.info("Payment validation result: {}", isSuccessful ? "SUCCESS" : "FAILED");
//        return isSuccessful;
//    }

    // Create payment entity from DTO
    public PaymentEntity createPaymentRecord(PaymentRequestDTO request, String transactionId, String status) {
        PaymentEntity payment = new PaymentEntity();
        payment.setUserId(request.getUserId());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(status);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }

    // Create response DTO from entity
    public PaymentResponseDTO createPaymentResponse(PaymentEntity payment, String status, String message) {
        return new PaymentResponseDTO(
            payment.getId(),
            payment.getUserId(),
            payment.getOrderId(),
            payment.getAmount(),
            status,
            payment.getTransactionId(),
            message,
            payment.getPaymentDate()
        );
    }

    // Update order status
    public void updateOrderStatus(Long orderId, String status) {
    	try {
            String url = ORDER_SERVICE_BASE_URL + "/" + orderId + "/status";
            
            OrderStatusUpdateRequestDTO updateRequest = new OrderStatusUpdateRequestDTO(orderId, status);
            
            String response = webClientService.put(url, updateRequest, String.class);
            
            log.info("Order status updated to {} for order: {}. Response: {}", status, orderId, response);
            
        } catch (Exception e) {
            log.error("Failed to update order status for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Failed to update order status: " + e.getMessage());
        }
    }

    // Send RabbitMQ message
    public void sendPaymentSuccessMessage(PaymentRequestDTO paymentRequest, 
                                         String transactionId, Long paymentId) {
        try {
            PaymentSuccessMessageDTO message = new PaymentSuccessMessageDTO(
                paymentRequest.getUserId(),
                paymentRequest.getOrderId(),
                paymentRequest.getAmount(),
                transactionId,
                LocalDateTime.now()
            );
            rabbitMQProducer.sendPaymentSuccessMessage(message);
        } catch (Exception e) {
            log.error("Failed to send RabbitMQ message for payment {}: {}", paymentId, e.getMessage());
            // Don't throw exception here as payment is already processed
        }
    }

    // Additional helper methods can be added here
    public PaymentEntity createFailedPaymentRecord(PaymentRequestDTO request) {
        return createPaymentRecord(request, 
            "FAILED_" + System.currentTimeMillis(), 
            "FAILED");
    }

    public boolean isPaymentSuccessful(PaymentEntity payment) {
        return "SUCCESS".equalsIgnoreCase(payment.getStatus());
    }
}