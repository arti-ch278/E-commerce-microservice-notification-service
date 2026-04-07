package com.artichourey.ecommerce.notificationservice.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
	
	@Value("${twilio.from-number}")
	private String fromNumber;
	
	
	
	public void sendSms(String to, String message) {

	    log.info("DEBUG SMS -> to: {} | from: {} | message: {}", to, fromNumber, message);

	    if (to == null || fromNumber == null || message == null) {
	        log.error("SMS failed due to null values");
	        return;
	    }

	    Message.creator(
	            new PhoneNumber(to),
	            new PhoneNumber(fromNumber),
	            message
	    ).create();

	    log.info("SMS sent successfully!");
	}
}