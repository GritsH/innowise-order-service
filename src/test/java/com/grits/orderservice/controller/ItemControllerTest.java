package com.grits.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.model.request.item.CreateItemRequest;
import com.grits.orderservice.model.request.item.UpdateItemRequest;
import com.grits.orderservice.repository.ItemRepository;
import com.grits.orderservice.util.JwtTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    void clean() {
        itemRepository.deleteAll();
        wireMock.resetAll();
    }

    @Test
    @DisplayName("should create item")
    void createItem() {
        try {
            CreateItemRequest request = createItemRequest();

            mockMvc.perform(post("/v1/items")
                            .with(JwtTestUtils.admin())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("item"))
                    .andExpect(jsonPath("$.price").value(99.00));

            assertThat(itemRepository.count()).isEqualTo(1);

            Item saved = itemRepository.findAll().getFirst();

            assertThat(saved.getName()).isEqualTo("item");
            assertThat(saved.getPrice()).isEqualByComparingTo("99.00");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return item by id")
    void getItemById() {
        try {
            Item item = createTestItem("item");

            mockMvc.perform(get("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.admin()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(item.getId().toString()))
                    .andExpect(jsonPath("$.name").value(item.getName()))
                    .andExpect(jsonPath("$.price").value(99.00));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return all items")
    void getAllItems() {
        try {
            createTestItem("item");
            createTestItem("item2");

            mockMvc.perform(get("/v1/items")
                            .with(JwtTestUtils.admin()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should update item")
    void updateItem() {
        try {
            Item item = createTestItem("item");

            UpdateItemRequest request = updateItemRequest();

            mockMvc.perform(patch("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.admin())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(item.getId().toString()))
                    .andExpect(jsonPath("$.name").value("Updated item"))
                    .andExpect(jsonPath("$.price").value(999.00));

            Item updated = itemRepository.findById(item.getId()).orElseThrow();

            assertThat(updated.getName()).isEqualTo("Updated item");
            assertThat(updated.getPrice()).isEqualByComparingTo("999.00");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should delete item")
    void deleteItem() {
        try {
            Item item = createTestItem("item");

            mockMvc.perform(delete("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.admin()))
                    .andExpect(status().isNoContent());

            assertThat(itemRepository.existsById(item.getId())).isFalse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user creates item")
    void throw403WhenUserCreatesItem() {
        try {
            CreateItemRequest request = createItemRequest();

            mockMvc.perform(post("/v1/items")
                            .with(JwtTestUtils.user(UUID.randomUUID(), "john@gmail.com"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user gets item by id")
    void throw403WhenUserGetsItemById() {
        try {
            Item item = createTestItem("item");

            mockMvc.perform(get("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.user(UUID.randomUUID(), "john@gmail.com")))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user gets all items")
    void throw403WhenUserGetsAllItems() {
        try {
            mockMvc.perform(get("/v1/items")
                            .with(JwtTestUtils.user(UUID.randomUUID(), "john@gmail.com")))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user updates item")
    void throw403WhenUserUpdatesItem() {
        try {
            Item item = createTestItem("item");
            UpdateItemRequest request = updateItemRequest();

            mockMvc.perform(patch("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.user(UUID.randomUUID(), "john@gmail.com"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("should return 403 when user deletes item")
    void throw403WhenUserDeletesItem() {
        try {
            Item item = createTestItem("item");

            mockMvc.perform(delete("/v1/items/{id}", item.getId())
                            .with(JwtTestUtils.user(UUID.randomUUID(), "john@gmail.com")))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CreateItemRequest createItemRequest() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("item");
        request.setPrice(BigDecimal.valueOf(99));
        return request;
    }

    private UpdateItemRequest updateItemRequest() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Updated item");
        request.setPrice(BigDecimal.valueOf(999));
        return request;
    }

    private Item createTestItem(String name) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(99));
        return itemRepository.save(item);
    }

}