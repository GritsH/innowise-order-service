package com.grits.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.OrderItem;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.model.request.order.CreateOrderRequest;
import com.grits.orderservice.model.request.OrderItemRequest;
import com.grits.orderservice.model.request.order.UpdateOrderRequest;
import com.grits.orderservice.repository.ItemRepository;
import com.grits.orderservice.repository.OrderRepository;
import com.grits.orderservice.util.JwtTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static final String USER_EMAIL = "john@gmail.com";
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void clean() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        wireMock.resetAll();

        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(99));
        item.setName("Order item");
        itemRepository.save(item);
    }

    @Test
    @DisplayName("should create order")
    void createOrder() {
        try {
            stubUserByEmail();
            CreateOrderRequest request = createOrderRequest();

            mockMvc.perform(post("/v1/orders")
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("CREATED"))
                    .andExpect(jsonPath("$.user.email").value(USER_EMAIL));

            assertThat(orderRepository.count()).isEqualTo(1);

            Order saved = orderRepository.findAll().getFirst();

            assertThat(saved.getUserId()).isEqualTo(USER_ID);
            assertThat(saved.isDeleted()).isFalse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return order by id")
    void getOrderById() {
        try {
            stubUserById();
            stubUserByEmail();
            Order order = createOrder(USER_ID);

            mockMvc.perform(get("/v1/orders/{id}", order.getId())
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(order.getId().toString()))
                    .andExpect(jsonPath("$.user.email").value(USER_EMAIL));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return all orders")
    void getAllOrders() {
        try {
            stubUserById();
            createOrder(USER_ID);
            createOrder(USER_ID);

            mockMvc.perform(get("/v1/orders")
                            .with(JwtTestUtils.admin()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return orders of current user")
    void getOrdersOfCurrentUser() {
        try {
            stubUserByEmail();
            createOrder(USER_ID);

            mockMvc.perform(get("/v1/orders/user")
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].user.email").value(USER_EMAIL));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should update order")
    void updateOrder() {
        try {
            stubUserById();
            stubUserByEmail();
            Order order = createOrder(USER_ID);
            UpdateOrderRequest request = updateOrderRequest();

            mockMvc.perform(put("/v1/orders/{id}", order.getId())
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("DELIVERED"));

            Order updated = orderRepository.findById(order.getId()).orElseThrow();

            assertThat(updated.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should delete order")
    void deleteOrder() {
        try {
            stubUserByEmail();
            Order order = createOrder(USER_ID);

            mockMvc.perform(delete("/v1/orders/{id}", order.getId())
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL)))
                    .andExpect(status().isNoContent());

            Order deleted = orderRepository.findById(order.getId()).orElseThrow();

            assertThat(deleted.isDeleted()).isTrue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user accesses another user's order")
    void throw403WhenUserAccessesAnotherUserOrder() {
        try {
            stubUserByEmail();
            Order order = createOrder(UUID.randomUUID());

            mockMvc.perform(get("/v1/orders/{id}", order.getId())
                            .with(JwtTestUtils.user(USER_ID, USER_EMAIL)))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CreateOrderRequest createOrderRequest() {
        Item item = getItem();
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(List.of(new OrderItemRequest(item.getId(), 2)));
        return request;
    }

    private UpdateOrderRequest updateOrderRequest() {
        Item item = getItem();
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(OrderStatus.DELIVERED);
        request.setItems(List.of(new OrderItemRequest(item.getId(), 3)));
        return request;
    }

    private Order createOrder(UUID userId) {
        Item item = getItem();

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setDeleted(false);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);

        order.setOrderItems(new ArrayList<>(List.of(orderItem)));
        order.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(2)));
        return orderRepository.save(order);
    }

    private Item getItem() {
        return itemRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("No items found in database"));
    }

    private void stubUserByEmail() {
        wireMock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/v1/users/by-email"))
                .withQueryParam("email", equalTo(USER_EMAIL))
                .willReturn(okJson("""
                        {
                          "id":"%s",
                          "name":"John",
                          "surname":"Doe",
                          "email":"%s"
                        }
                        """.formatted(USER_ID, USER_EMAIL))));
    }

    private void stubUserById() {
        wireMock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/v1/users/" + USER_ID))
                .willReturn(okJson("""
                        {
                          "id":"%s",
                          "name":"John",
                          "surname":"Doe",
                          "email":"%s"
                        }
                        """.formatted(USER_ID, USER_EMAIL))));
    }
}