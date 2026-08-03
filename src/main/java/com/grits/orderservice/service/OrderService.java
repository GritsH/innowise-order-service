package com.grits.orderservice.service;

import com.grits.orderservice.client.UserServiceClient;
import com.grits.orderservice.dao.ItemDao;
import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.OrderItem;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.mapper.OrderMapper;
import com.grits.orderservice.model.request.CreateOrderRequest;
import com.grits.orderservice.model.request.OrderItemRequest;
import com.grits.orderservice.model.request.UpdateOrderRequest;
import com.grits.orderservice.model.response.OrderResponse;
import com.grits.orderservice.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;

    private final ItemDao itemDao;

    private final UserServiceClient userServiceClient;

    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        UserResponse userResponse = userServiceClient.getUserByEmail(request.getEmail());
        Order order = orderMapper.toEntity(request);
        order.setUserId(userResponse.getId());

        List<OrderItem> orderItems = createOrderItems(order, request.getItems());
        order.setOrderItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));
        Order savedOrder = orderDao.save(order);
        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setUser(userResponse);

        return response;
    }

    @Transactional
    public OrderResponse updateOrder(UUID id, UpdateOrderRequest request) {
        Order order = orderDao.getOrderById(id);
        order.setStatus(request.getStatus());
        order.getOrderItems().clear();

        List<OrderItem> orderItems = createOrderItems(order, request.getItems());
        order.getOrderItems().addAll(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));

        Order updatedOrder = orderDao.save(order);
        UserResponse userResponse = userServiceClient.getUserById(updatedOrder.getUserId());
        OrderResponse response = orderMapper.toResponse(updatedOrder);
        response.setUser(userResponse);
        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderDao.getOrderById(id);
        UserResponse userResponse = userServiceClient.getUserById(order.getUserId());
        OrderResponse response = orderMapper.toResponse(order);
        response.setUser(userResponse);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(LocalDateTime from, LocalDateTime to, List<OrderStatus> statuses, int page, int size) {
        Page<Order> orders = orderDao.getAllOrders(from, to, statuses, page, size);
        return orders.map(order -> {
            UserResponse user = userServiceClient.getUserById(order.getUserId());
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(user);
            return response;
        });
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserEmail(String email) {
        UserResponse userResponse = userServiceClient.getUserByEmail(email);
        return orderDao.getOrdersByUserId(userResponse.getId())
                .stream()
                .map(order -> {
                    OrderResponse response = orderMapper.toResponse(order);
                    response.setUser(userResponse);
                    return response;
                })
                .toList();
    }

    @Transactional
    public void deleteOrder(UUID id) {
        orderDao.deleteOrder(id);
    }

    private List<OrderItem> createOrderItems(Order order, List<OrderItemRequest> requests) {
        return requests.stream()
                .map(request -> createOrderItem(order, request))
                .toList();
    }

    private OrderItem createOrderItem(Order order, OrderItemRequest request) {
        Item item = itemDao.getItemById(request.getItemId());
        return new OrderItem(UUID.randomUUID(), order, item, request.getQuantity());
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::calculateOrderItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateOrderItemPrice(OrderItem orderItem) {
        return orderItem.getItem()
                .getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
}
