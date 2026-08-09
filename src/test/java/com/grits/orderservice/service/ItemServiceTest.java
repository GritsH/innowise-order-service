package com.grits.orderservice.service;

import com.grits.orderservice.dao.ItemDao;
import com.grits.orderservice.entity.Item;
import com.grits.orderservice.mapper.ItemMapper;
import com.grits.orderservice.model.request.item.CreateItemRequest;
import com.grits.orderservice.model.request.item.UpdateItemRequest;
import com.grits.orderservice.model.response.item.ItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemDao itemDao;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private CreateItemRequest createItemRequest;
    private UpdateItemRequest updateItemRequest;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        item = mock(Item.class);
        createItemRequest = mock(CreateItemRequest.class);
        updateItemRequest = mock(UpdateItemRequest.class);
        itemResponse = mock(ItemResponse.class);
    }

    @Test
    @DisplayName("should create item")
    void createItem() {
        when(itemMapper.toEntity(createItemRequest)).thenReturn(item);
        when(itemDao.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);

        ItemResponse result = itemService.createItem(createItemRequest);

        assertThat(result).isSameAs(itemResponse);

        verify(itemMapper).toEntity(createItemRequest);
        verify(itemDao).save(item);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("should update item")
    void updateItem() {
        UUID id = UUID.randomUUID();

        when(itemDao.getItemById(id)).thenReturn(item);
        when(itemDao.saveUpdatedItem(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);

        ItemResponse result = itemService.updateItem(id, updateItemRequest);

        assertThat(result).isSameAs(itemResponse);

        verify(itemDao).getItemById(id);
        verify(itemMapper).updateEntity(updateItemRequest, item);
        verify(itemDao).saveUpdatedItem(item);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("should return item by id")
    void getItemById() {
        UUID id = UUID.randomUUID();

        when(itemDao.getItemById(id)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);

        ItemResponse result = itemService.getItemById(id);

        assertThat(result).isSameAs(itemResponse);

        verify(itemDao).getItemById(id);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("should return all items")
    void getAllItems() {
        when(itemDao.getAllItems()).thenReturn(List.of(item));
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);

        List<ItemResponse> result = itemService.getAllItems();

        assertThat(result).containsExactly(itemResponse);

        verify(itemDao).getAllItems();
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("should return empty list when there are no items")
    void getAllItemsWhenEmpty() {
        when(itemDao.getAllItems()).thenReturn(List.of());

        List<ItemResponse> result = itemService.getAllItems();

        assertThat(result).isEmpty();

        verify(itemDao).getAllItems();
        verifyNoInteractions(itemMapper);
    }

    @Test
    @DisplayName("should delete item")
    void deleteItem() {
        UUID id = UUID.randomUUID();

        itemService.deleteItem(id);

        verify(itemDao).deleteItem(id);
    }
}