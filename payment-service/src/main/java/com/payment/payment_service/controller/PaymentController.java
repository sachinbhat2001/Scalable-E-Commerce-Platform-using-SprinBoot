package com.payment.payment_service.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.payment.payment_service.entity.PaymentRequestDTO;
import com.payment.payment_service.entity.PaymentResponseDTO;
import com.payment.payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;


    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        try {
            log.info("Received payment processing request for order: {}", paymentRequest.getOrderId());
            PaymentResponseDTO response = paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in processPayment endpoint: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(paymentRequest, "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        try {
            log.info("Fetching payment details for payment ID: {}", paymentId);
            PaymentResponseDTO response = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Payment not found with ID {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Payment not found with ID: " + paymentId));
        } catch (Exception e) {
            log.error("Error fetching payment with ID {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching payment: " + e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            log.info("Fetching payment details for order ID: {}", orderId);
            PaymentResponseDTO response = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Payment not found for order ID {}: {}", orderId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Payment not found for order ID: " + orderId));
        } catch (Exception e) {
            log.error("Error fetching payment for order ID {}: {}", orderId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching payment: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running and healthy");
    }

    // Helper method to create error response for processPayment
    private PaymentResponseDTO createErrorResponse(PaymentRequestDTO paymentRequest, String errorMessage) {
        return new PaymentResponseDTO(
            null,
            paymentRequest.getUserId(),
            paymentRequest.getOrderId(),
            paymentRequest.getAmount(),
            "ERROR",
            null,
            errorMessage,
            null
        );
    }

    // Helper method to create error response for get methods
    private PaymentResponseDTO createErrorResponse(String errorMessage) {
        return new PaymentResponseDTO(
            null,
            null,
            null,
            null,
            "ERROR",
            null,
            errorMessage,
            null
        );
    }
    @GetMapping("/test-rabbitmq")
    public ResponseEntity<String> testRabbitMQ() {
        try {
            // Test if RabbitMQ is reachable
            rabbitTemplate.convertAndSend("payment_exchange", "payment.success", 
                "Test message from Payment Service");
            
            return ResponseEntity.ok("RabbitMQ connection successful!");
        } catch (Exception e) {
            log.error("RabbitMQ connection failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("RabbitMQ connection failed: " + e.getMessage());
        }
    }
}