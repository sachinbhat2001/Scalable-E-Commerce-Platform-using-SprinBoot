package com.payment.payment_service.rabbitmq;
import com.payment.payment_service.entity.PaymentSuccessMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.payment.payment_service.config.RabbitMQConfig.PAYMENT_EXCHANGE;
import static com.payment.payment_service.config.RabbitMQConfig.PAYMENT_ROUTING_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPaymentSuccessMessage(PaymentSuccessMessageDTO message) {
        try {
            rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, message);
            log.info("Payment success message sent: {}", message);
        } catch (Exception e) {
            log.error("Failed to send payment success message: {}", e.getMessage());
            throw new RuntimeException("Failed to send RabbitMQ message", e);
        }
    }
}