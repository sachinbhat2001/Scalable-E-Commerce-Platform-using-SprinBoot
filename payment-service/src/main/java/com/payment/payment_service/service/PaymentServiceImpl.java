package com.payment.payment_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.payment_service.client.OrderServiceClient;
import com.payment.payment_service.entity.PaymentEntity;
import com.payment.payment_service.entity.PaymentRequestDTO;
import com.payment.payment_service.entity.PaymentResponseDTO;
import com.payment.payment_service.rabbitmq.RabbitMQProducer;
import com.payment.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
	@Autowired
	private PaymentRepository paymentRepository;
//	@Autowired
//    private OrderServiceClient orderServiceClient;
	@Autowired
    private RabbitMQProducer rabbitMQProducer;
	
	private final PaymentServiceHelperMethods helperMethods;
	@Override
	public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
		try {
            log.info("Processing payment for user: {}, order: {}", 
                    paymentRequest.getUserId(), paymentRequest.getOrderId());

            // Generate transaction ID using helper
            String transactionId = helperMethods.generateTransactionId();
            
            // Validate payment using helper
            boolean isPaymentSuccessful = helperMethods.validatePayment(paymentRequest);
            
            // Create payment record using helper
            PaymentEntity payment = helperMethods.createPaymentRecord(paymentRequest, transactionId, 
                                                isPaymentSuccessful ? "SUCCESS" : "FAILED");
            
            PaymentEntity savedPayment = paymentRepository.save(payment);
            
            if (isPaymentSuccessful) {
                // Update order status using helper
                helperMethods.updateOrderStatus(paymentRequest.getOrderId(), "SUCCESSFUL");
                
                // Send message to RabbitMQ using helper
                helperMethods.sendPaymentSuccessMessage(paymentRequest, transactionId, savedPayment.getId());
                
                log.info("Payment processed successfully for order: {}", paymentRequest.getOrderId());
                
                return helperMethods.createPaymentResponse(savedPayment, "SUCCESS", "Payment processed successfully");
            } else {
                log.warn("Payment failed for order: {}", paymentRequest.getOrderId());
                return helperMethods.createPaymentResponse(savedPayment, "FAILED", "Payment processing failed");
            }
            
        } catch (Exception e) {
            log.error("Error processing payment for order {}: {}", 
                     paymentRequest.getOrderId(), e.getMessage());
            
            // Save failed payment record using helper
            PaymentEntity failedPayment = helperMethods.createFailedPaymentRecord(paymentRequest);
            PaymentEntity savedFailedPayment = paymentRepository.save(failedPayment);
            
            return helperMethods.createPaymentResponse(savedFailedPayment, "FAILED", 
                                       "Error processing payment: " + e.getMessage());
        }
		
	}
	@Override
	public PaymentResponseDTO getPaymentById(Long paymentId) {
		try {
            PaymentEntity payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
            
            return helperMethods.createPaymentResponse(payment, payment.getStatus(), "Payment details retrieved");
        } catch (Exception e) {
            log.error("Error retrieving payment with id {}: {}", paymentId, e.getMessage());
            throw new RuntimeException("Error retrieving payment: " + e.getMessage());
        }
	}
	@Override
	public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
		try {
            PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for order id: " + orderId));
            
            return helperMethods.createPaymentResponse(payment, payment.getStatus(), "Payment details retrieved");
        } catch (Exception e) {
            log.error("Error retrieving payment for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Error retrieving payment: " + e.getMessage());
        }
    
	}
	
	
}
