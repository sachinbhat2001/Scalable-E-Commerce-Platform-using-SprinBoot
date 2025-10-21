package com.payment.payment_service.rabbitmq;

import com.payment.payment_service.entity.PaymentSuccessMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:payment_exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:payment.success}")
    private String routingKey;

    public void sendPaymentSuccessMessage(PaymentSuccessMessageDTO message) {
        try {
            log.info("🟡 RABBITMQ: Sending payment success message for order: {}", message.getOrderId());
            
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            
            log.info("✅ RABBITMQ: Payment success message sent for order: {}", message.getOrderId());
            log.info("✅ RABBITMQ: Message details: {}", message);
            
        } catch (Exception e) {
            log.error("❌ RABBITMQ: Failed to send payment success message for order {}: {}", 
                     message.getOrderId(), e.getMessage());
            throw new RuntimeException("Failed to send RabbitMQ message", e);
        }
    }
}