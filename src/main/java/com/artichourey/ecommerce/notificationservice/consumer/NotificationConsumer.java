package com.artichourey.ecommerce.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.notificationservice.dto.NotificationEvent;
import com.artichourey.ecommerce.notificationservice.service.EmailService;
import com.artichourey.ecommerce.notificationservice.service.SmsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class NotificationConsumer {

	private final EmailService emailService;
	
	private final SmsService smsService;
	
	@KafkaListener(topics="Notification-topic" , groupId="notification-group")
	public void consume(NotificationEvent event) {
		if("EMAIL".equals(event.getType())) {
		emailService.sendMail(event.getEmail(), event.getSubject(), event.getMessage());
	}
		if("SMS".equals(event.getType())) {
			smsService.sendSms(event.getMobile(), event.getMessage());
		}
	}
}
