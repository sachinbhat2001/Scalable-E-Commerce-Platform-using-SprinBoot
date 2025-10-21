package com.payment.payment_service.rabbitmq;

import com.payment.payment_service.entity.PaymentSuccessMessageDTO;
import com.payment.payment_service.service.EmailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
//@Slf4j
//public class RabbitMQConsumer {
//	private final EmailService emailService = new EmailService();
//
//
//    @RabbitListener(queues = {"${rabbitmq.queue.name:payment_success_queue}"})
//    public void consumePaymentSuccessMessage(PaymentSuccessMessageDTO message) {
//        log.info("RABBITMQ CONSUMER: Received payment success message: {}", message);
//     // Send email to customer
//        emailService.sendPaymentConfirmation(message.getUserId(), message.getOrderId());
//        
//        // Here you can:
//        // - Send email confirmation
//        // - Update inventory
//        // - Trigger shipping process
//        // - Notify other services
//        
//        log.info("Processing payment success for order: {}", message.getOrderId());
//    }
//    
//}
@Service
@Slf4j
public class RabbitMQConsumer {
    
    private final EmailService emailService;

    
    public RabbitMQConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.name:payment_success_queue}"})
    public void consumePaymentSuccessMessage(PaymentSuccessMessageDTO message) {
        log.info("RABBITMQ CONSUMER: Received payment success message: {}", message);
        emailService.sendPaymentConfirmation(message.getUserId(), message.getOrderId());
        
        log.info("Processing payment success for order: {}", message.getOrderId());
    }
}
//@Service
//@Slf4j
//public class RabbitMQConsumer {
//    
//    private final EmailService emailService;
//
//    // Use constructor injection instead of new
//    public RabbitMQConsumer(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    @RabbitListener(queues = {"${rabbitmq.queue.name:payment_success_queue}"})
//    public void consumePaymentSuccessMessage(PaymentSuccessMessageDTO message) {
//        log.info("RABBITMQ CONSUMER: Received payment success message: {}", message);
//        
//        // Send email to customer
//        try {
//            emailService.sendPaymentConfirmation(message.getUserId(), message.getOrderId());
//        } catch (Exception e) {
//            log.error("Failed to send email for order {}: {}", message.getOrderId(), e.getMessage());
//        }
//        
//        log.info("Processing payment success for order: {}", message.getOrderId());
//    }
//}