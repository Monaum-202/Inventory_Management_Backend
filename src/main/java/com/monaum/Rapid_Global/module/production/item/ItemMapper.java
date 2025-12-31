package com.monaum.Rapid_Global.module.production.item;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    // Entity → Response
    ItemResponseDTO toResponse(Item item);
    List<ItemResponseDTO> toResponseList(List<Item> items);

    // Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemUsage", ignore = true)
    Item toEntity(ItemDTO dto);

    List<Item> toEntityList(List<ItemDTO> dtoList);
}
