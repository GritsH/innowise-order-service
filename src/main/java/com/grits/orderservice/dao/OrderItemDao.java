package com.grits.orderservice.dao;

import com.grits.orderservice.entity.OrderItem;
import com.grits.orderservice.exception.OrderItemNotFoundException;
import com.grits.orderservice.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderItemDao {

    private final OrderItemRepository orderItemRepository;

    public OrderItem save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem saveUpdatedOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemById(UUID id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new OrderItemNotFoundException(id));
    }

    public List<OrderItem> getOrderItemsByOrderId(UUID orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    public void deleteOrderItem(UUID id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItemRepository.delete(orderItem);
    }
}
