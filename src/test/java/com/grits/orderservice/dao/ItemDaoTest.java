package com.grits.orderservice.dao;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.exception.ItemNotFoundException;
import com.grits.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemDaoTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemDao itemDao;

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
}