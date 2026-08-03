package com.grits.orderservice.mapper;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.model.response.ItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemResponse toResponse(Item item);

    List<ItemResponse> toResponse(List<Item> items);
}
