package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.exception.ItemAlreadyExistsException;
import com.grits.orderservice.exception.ItemNotFoundException;
import com.grits.orderservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemDao {

    private final ItemRepository itemRepository;

    public Item save(Item item) {
        if (itemRepository.existsByName(item.getName())) {
            throw new ItemAlreadyExistsException(item.getName());
        }
        return itemRepository.save(item);
    }

    public Item saveUpdatedItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(UUID id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll(Sort.by("name").ascending());
    }

    public void deleteItem(UUID id) {
        Item item = getItemById(id);
        itemRepository.delete(item);
    }
}
