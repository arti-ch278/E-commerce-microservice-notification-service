package com.artichourey.ecommerce.notificationservice.dto;

import lombok.Data;

@Data
public class NotificationEvent {
	
	private String userId;    
    private String email;    
    private String mobile;   
    private String type;      // "EMAIL" or "SMS"
    private String subject;
    private String message;

}
