package com.grits.orderservice.mapper;

import com.grits.orderservice.entity.Order;
import com.grits.orderservice.model.request.CreateOrderRequest;
import com.grits.orderservice.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "userId", ignore = true)
    Order toEntity(CreateOrderRequest request);
}
