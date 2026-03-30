package com.artichourey.ecommerce.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.artichourey.ecommerce.notificationservice.dto.UserResponseDto;

@FeignClient(name = "user-service", url = "http://localhost:8086")
public interface UserServiceClient {

    // Call the public endpoint (no auth required)
    @GetMapping("/api/users/public/{id}")
    UserResponseDto getById(@PathVariable("id") Long id);
}
