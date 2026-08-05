package com.grits.orderservice.mapper;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.model.response.ItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemResponse toResponse(Item item);
}
