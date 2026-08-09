package com.grits.orderservice.service;

import com.grits.orderservice.client.UserServiceClient;
import com.grits.orderservice.dao.ItemDao;
import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.OrderItem;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.mapper.OrderMapper;
import com.grits.orderservice.model.request.order.CreateOrderRequest;
import com.grits.orderservice.model.request.OrderItemRequest;
import com.grits.orderservice.model.request.order.UpdateOrderRequest;
import com.grits.orderservice.model.response.order.OrderResponse;
import com.grits.orderservice.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        UserResponse userResponse = userServiceClient.getUserByEmail(userEmail);
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
        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders.getContent()) {
            UserResponse user = userServiceClient.getUserById(order.getUserId());
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(user);
            responses.add(response);
        }
        return new PageImpl<>(responses, orders.getPageable(), orders.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserEmail(String email) {
        UserResponse userResponse = userServiceClient.getUserByEmail(email);
        List<Order> orders = orderDao.getOrdersByUserId(userResponse.getId());
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(userResponse);
            responses.add(response);
        }
        return responses;
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

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(request.getQuantity());
        return orderItem;
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
