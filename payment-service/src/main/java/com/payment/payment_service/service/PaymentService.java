package com.payment.payment_service.service;

import com.payment.payment_service.entity.PaymentRequestDTO;
import com.payment.payment_service.entity.PaymentResponseDTO;

public interface PaymentService {
	/**
     * Process payment for an order
     * @param paymentRequest the payment request containing order details, amount, etc.
     * @return PaymentResponseDTO containing payment processing result
     */
    PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest);

    /**
     * Retrieve payment details by payment ID
     * @param paymentId the unique identifier of the payment
     * @return PaymentResponseDTO containing payment details
     */
    PaymentResponseDTO getPaymentById(Long paymentId);

    /**
     * Retrieve payment details by order ID
     * @param orderId the unique identifier of the order
     * @return PaymentResponseDTO containing payment details
     */
    PaymentResponseDTO getPaymentByOrderId(Long orderId);

}
