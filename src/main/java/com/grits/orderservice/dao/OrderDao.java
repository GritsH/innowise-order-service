package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.exception.OrderNotFoundException;
import com.grits.orderservice.repository.OrderRepository;
import com.grits.orderservice.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderDao {

    private final OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id).filter(order -> !order.isDeleted()).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Page<Order> getAllOrders(LocalDateTime from, LocalDateTime to, List<OrderStatus> statuses, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<Order> specification = Specification.allOf(
                OrderSpecification.notDeleted(),
                OrderSpecification.createdFrom(from),
                OrderSpecification.createdTo(to),
                OrderSpecification.hasStatuses(statuses)
        );
        return orderRepository.findAll(specification, pageable);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        Specification<Order> specification = Specification.allOf(
                OrderSpecification.notDeleted(),
                OrderSpecification.hasUserId(userId)
        );
        return orderRepository.findAll(specification);
    }

    public Order deleteOrder(UUID id) {
        Order order = getOrderById(id);
        order.setDeleted(true);
        return orderRepository.save(order);
    }
}
