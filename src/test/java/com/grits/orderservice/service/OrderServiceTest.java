package com.grits.orderservice.service;

import com.grits.orderservice.client.UserServiceClient;
import com.grits.orderservice.dao.ItemDao;
import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.mapper.OrderMapper;
import com.grits.orderservice.model.request.CreateOrderRequest;
import com.grits.orderservice.model.request.OrderItemRequest;
import com.grits.orderservice.model.request.UpdateOrderRequest;
import com.grits.orderservice.model.response.OrderResponse;
import com.grits.orderservice.model.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ItemDao itemDao;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("should create order")
    void saveOrder() {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(List.of(new OrderItemRequest(itemId, 2)));
        UserResponse user = new UserResponse();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setPrice(BigDecimal.valueOf(25));
        Order order = new Order();
        Order savedOrder = new Order();
        savedOrder.setUserId(userId);
        OrderResponse response = new OrderResponse();

        when(userServiceClient.getUserByEmail("john@gmail.com")).thenReturn(user);
        when(orderMapper.toEntity(request)).thenReturn(order);
        when(itemDao.getItemById(itemId)).thenReturn(item);
        when(orderDao.save(order)).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        OrderResponse result = orderService.createOrder(request, "john@gmail.com");

        assertThat(result).isSameAs(response);
        assertThat(result.getUser()).isSameAs(user);
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("50");
    }

    @Test
    @DisplayName("should update order")
    void updateOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.DELIVERED, List.of(new OrderItemRequest(itemId, 3)));
        Item item = new Item();
        item.setId(itemId);
        item.setPrice(BigDecimal.TEN);
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderItems(new ArrayList<>());
        UserResponse user = new UserResponse();
        user.setId(userId);
        OrderResponse response = new OrderResponse();

        when(orderDao.getOrderById(orderId)).thenReturn(order);
        when(itemDao.getItemById(itemId)).thenReturn(item);
        when(orderDao.save(order)).thenReturn(order);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.updateOrder(orderId, request);

        assertThat(result).isSameAs(response);
        assertThat(result.getUser()).isSameAs(user);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("30");
    }

    @Test
    @DisplayName("should return order by id")
    void getOrderById() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Order order = new Order();
        order.setUserId(userId);
        UserResponse user = new UserResponse();
        OrderResponse response = new OrderResponse();

        when(orderDao.getOrderById(id)).thenReturn(order);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.getOrderById(id);

        assertThat(result).isSameAs(response);
        assertThat(result.getUser()).isSameAs(user);
    }

    @Test
    @DisplayName("should return orders by user email")
    void getOrdersByUserEmail() {
        UUID userId = UUID.randomUUID();
        UserResponse user = new UserResponse();
        user.setId(userId);
        Order order = new Order();
        OrderResponse response = new OrderResponse();

        when(userServiceClient.getUserByEmail("john@test.com")).thenReturn(user);
        when(orderDao.getOrdersByUserId(userId)).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(response);

        List<OrderResponse> result = orderService.getOrdersByUserEmail("john@test.com");

        assertThat(result).hasSize(1).containsExactly(response);
        assertThat(result.getFirst().getUser()).isSameAs(user);
    }

    @Test
    @DisplayName("should delete order")
    void deleteOrder() {
        UUID id = UUID.randomUUID();

        orderService.deleteOrder(id);

        verify(orderDao).deleteOrder(id);
    }

}