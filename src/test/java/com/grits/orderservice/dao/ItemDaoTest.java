package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.exception.ItemAlreadyExistsException;
import com.grits.orderservice.exception.ItemNotFoundException;
import com.grits.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemDaoTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemDao itemDao;

    @Test
    @DisplayName("should save item")
    void save() {
        Item item = new Item();
        item.setName("item");

        when(itemRepository.existsByName(item.getName())).thenReturn(false);
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemDao.save(item);

        assertThat(result).isSameAs(item);

        verify(itemRepository).existsByName(item.getName());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    @DisplayName("should throw exception when name already exists")
    void throwExceptionWhenItemAlreadyExists() {
        Item item = new Item();
        item.setName("item");

        when(itemRepository.existsByName(item.getName())).thenReturn(true);

        assertThatThrownBy(() -> itemDao.save(item)).isInstanceOf(ItemAlreadyExistsException.class);

        verify(itemRepository).existsByName(item.getName());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    @DisplayName("should save updated item")
    void saveUpdatedItem() {
        Item item = new Item();
        item.setName("item");

        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemDao.saveUpdatedItem(item);

        assertThat(result).isSameAs(item);
    }

    @Test
    @DisplayName("should return item by id")
    void getItemById() {
        UUID id = UUID.randomUUID();
        Item item = new Item();
        item.setId(id);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Item result = itemDao.getItemById(id);

        assertThat(result).isSameAs(item);
    }

    @Test
    @DisplayName("should throw exception when item does not exist")
    void throwExceptionWhenItemDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemDao.getItemById(id)).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("should return all items")
    void getAllItems() {
        List<Item> items = List.of(new Item());

        when(itemRepository.findAll(Sort.by("name").ascending())).thenReturn(items);

        List<Item> result = itemDao.getAllItems();

        assertThat(result).isSameAs(items);
    }

    @Test
    @DisplayName("should delete item")
    void deleteItem() {
        UUID id = UUID.randomUUID();
        Item item = new Item();
        item.setId(id);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        itemDao.deleteItem(id);

        verify(itemRepository).delete(item);
    }
}