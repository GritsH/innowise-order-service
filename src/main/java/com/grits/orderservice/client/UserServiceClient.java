package com.grits.orderservice.client;

import com.grits.orderservice.config.FeignAuthConfiguration;
import com.grits.orderservice.model.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        configuration = FeignAuthConfiguration.class
)
public interface UserServiceClient {

    @GetMapping("/v1/users/by-email")
    UserResponse getUserByEmail(@RequestParam String email);

    @GetMapping("/v1/users/by-ids")
    List<UserResponse> getUsersByIds(@RequestParam List<UUID> ids);

    @GetMapping("/v1/users/{id}")
    UserResponse getUserById(@PathVariable UUID id);
}
