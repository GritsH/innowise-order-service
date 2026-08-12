package com.grits.orderservice.controller;

import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.model.request.order.CreateOrderRequest;
import com.grits.orderservice.model.request.order.UpdateOrderRequest;
import com.grits.orderservice.model.response.order.OrderResponse;
import com.grits.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) List<OrderStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> response = orderService.getAllOrders(from, to, statuses, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserEmail(@AuthenticationPrincipal Jwt jwt) {
        List<OrderResponse> response = orderService.getOrdersByUserEmail(jwt.getClaimAsString("email"));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        OrderResponse response = orderService.createOrder(request, jwt.getClaimAsString("email"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable UUID id, @Valid @RequestBody UpdateOrderRequest request) {
        OrderResponse response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
