package com.grits.orderservice.mapper;

import com.grits.orderservice.entity.Item;
import com.grits.orderservice.model.request.item.CreateItemRequest;
import com.grits.orderservice.model.request.item.UpdateItemRequest;
import com.grits.orderservice.model.response.item.ItemResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemResponse toResponse(Item item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Item toEntity(CreateItemRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateItemRequest request, @MappingTarget Item item);
}
