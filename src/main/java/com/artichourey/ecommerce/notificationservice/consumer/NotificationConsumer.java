package com.artichourey.ecommerce.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.events.OrderPlacedEvent;
import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;
import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.notificationservice.client.UserServiceClient;
import com.artichourey.ecommerce.notificationservice.dto.UserResponseDto;
import com.artichourey.ecommerce.notificationservice.service.EmailService;
import com.artichourey.ecommerce.notificationservice.service.SmsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void consumeOrderEvent(OrderPlacedEvent event) {
        sendNotification(event.getSkuCode(),"ORDER_PLACED",
                "Your order is placed successfully!");
    }

    @KafkaListener(topics = "stock-reserved-topic", groupId = "notification-group")
    public void consumeStockReserved(StockReservedEvent event) {
        sendNotification(event.getUserId(), "STOCK_RESERVED",
                "Stock reserved for product " + event.getSkuCode());
    }

    @KafkaListener(topics = "stock-failed-topic", groupId = "notification-group")
    public void consumeStockFailed(StockFailedEvent event) {
        sendNotification(
            event.getUserId(),
            "STOCK_FAILED",
            "Stock failed for order " + event.getOrderId() + ". Reason: " + event.getReason()
        );
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "notification-group")
    public void consumePaymentSuccess(PaymentCompletedEvent event) {
        sendNotification(event.getUserId(), "PAYMENT_SUCCESS",
                "Payment successful!");
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "notification-group")
    public void consumePaymentFailed(PaymentFailedEvent event) {
        sendNotification(event.getUserId(), "PAYMENT_FAILED",
                "Payment failed. Please try again.");
    }

    private void sendNotification(String userIdStr, String type, String message) {

        try {
           
            Long userId = Long.valueOf(userIdStr);

            // Call User Service
            UserResponseDto user = userServiceClient.getById(userId);

            String email = user.getEmail();
            String mobile = user.getPhone();

            // Decide notification type
            if ("PAYMENT_FAILED".equals(type) || "STOCK_FAILED".equals(type)) {
                smsService.sendSms(mobile, message);
                log.info("SMS sent to userId={} | message={}", userId, message);
            } else {
                emailService.sendMail(email, type, message);
                log.info("EMAIL sent to userId={} | message={}", userId, message);
            }

        } catch (NumberFormatException e) {
           
            log.error("Invalid userId received: {} | Cannot convert to Long", userIdStr);
        } catch (Exception e) {
            // Prevent Kafka listener from crashing
            log.error("Error while sending notification for userId={}", userIdStr, e);
        }
    }
}