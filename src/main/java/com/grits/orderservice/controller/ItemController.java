package com.grits.orderservice.controller;

import com.grits.orderservice.model.request.item.CreateItemRequest;
import com.grits.orderservice.model.request.item.UpdateItemRequest;
import com.grits.orderservice.model.response.item.ItemResponse;
import com.grits.orderservice.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> response = itemService.getAllItems();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable UUID id) {
        ItemResponse response = itemService.getItemById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable UUID id, @Valid @RequestBody UpdateItemRequest request) {
        ItemResponse response = itemService.updateItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
