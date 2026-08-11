package com.grits.orderservice.service;

import com.grits.orderservice.dao.ItemDao;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.mapper.ItemMapper;
import com.grits.orderservice.model.request.item.CreateItemRequest;
import com.grits.orderservice.model.request.item.UpdateItemRequest;
import com.grits.orderservice.model.response.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;
    private final ItemMapper itemMapper;

    public ItemResponse createItem(CreateItemRequest request) {
        Item item = itemMapper.toEntity(request);
        Item savedItem = itemDao.save(item);
        return itemMapper.toResponse(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(UUID id, UpdateItemRequest request) {
        Item item = itemDao.getItemById(id);
        itemMapper.updateEntity(request, item);
        Item updatedItem = itemDao.saveUpdatedItem(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(UUID id) {
        Item item = itemDao.getItemById(id);
        return itemMapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        return itemDao.getAllItems()
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteItem(UUID id) {
        itemDao.deleteItem(id);
    }
}
