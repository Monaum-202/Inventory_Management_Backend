package com.monaum.Rapid_Global.module.production.itemUsage;


import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface ItemUsageMapper {

    // Entity → Response
    ItemUsageResponseDTO toResponse(ItemUsage itemUsage);
    List<ItemUsageResponseDTO> toResponseList(List<ItemUsage> list);

    // Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "items", source = "items")
    ItemUsage toEntity(ItemUsageDTO dto);
}
