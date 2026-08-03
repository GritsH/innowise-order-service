package com.grits.orderservice.client;

import com.grits.orderservice.model.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}"
)
public interface UserServiceClient {

    @GetMapping("/v1/users/{email}")
    UserResponse getUserByEmail(@PathVariable String email);

    @GetMapping("/v1/users/{id}")
    UserResponse getUserById(@PathVariable UUID id);
}
