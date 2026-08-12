package com.grits.orderservice.model.request.order;

import com.grits.orderservice.model.request.OrderItemRequest;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateOrderRequest {

    @NotEmpty
    private List<OrderItemRequest> items;
}
