package com.grits.orderservice.mapper;

import com.grits.orderservice.entity.OrderItem;
import com.grits.orderservice.model.response.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface OrderItemMapper {

    @Mapping(source = "item", target = "item")
    OrderItemResponse toResponse(OrderItem orderItem);

    List<OrderItemResponse> toResponse(List<OrderItem> orderItems);
}
