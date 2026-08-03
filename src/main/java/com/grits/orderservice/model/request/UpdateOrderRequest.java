package com.grits.orderservice.model.request;

import com.grits.orderservice.entity.status.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

        @NotNull
        private OrderStatus status;

        @NotEmpty
        private List<OrderItemRequest> items;
}
