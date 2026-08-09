package com.grits.orderservice.model.response.order;

import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.model.response.OrderItemResponse;
import com.grits.orderservice.model.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;

    private OrderStatus status;

    private BigDecimal totalPrice;

    private List<OrderItemResponse> items;

    private UserResponse user;
}
