package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.exception.ItemNotFoundException;
import com.grits.orderservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemDao {

    private final ItemRepository itemRepository;

    public Item getItemById(UUID id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }
}
