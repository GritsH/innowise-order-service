package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Order;
import com.grits.orderservice.exception.OrderNotFoundException;
import com.grits.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDaoTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDao orderDao;

    @Test
    @DisplayName("should save order")
    void save() {
        Order order = new Order();

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderDao.save(order);

        assertThat(result).isSameAs(order);
    }

    @Test
    @DisplayName("should return order by id")
    void getOrderById() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setId(id);
        order.setDeleted(false);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order result = orderDao.getOrderById(id);

        assertThat(result).isSameAs(order);
    }

    @Test
    @DisplayName("should throw exception when order does not exist")
    void throwExceptionWhenOrderDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderDao.getOrderById(id)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("should throw exception when order is deleted")
    void throwExceptionWhenOrderIsDeleted() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setId(id);
        order.setDeleted(true);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderDao.getOrderById(id)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("should return orders by user id")
    void getOrdersByUserId() {
        UUID userId = UUID.randomUUID();
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findAll(any(Specification.class))).thenReturn(orders);

        List<Order> result = orderDao.getOrdersByUserId(userId);

        assertThat(result).hasSize(2).isSameAs(orders);
    }

    @Test
    @DisplayName("should delete order")
    void deleteOrder() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setId(id);
        order.setDeleted(false);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderDao.deleteOrder(id);

        assertThat(result.isDeleted()).isTrue();
        assertThat(result).isSameAs(order);
    }
}