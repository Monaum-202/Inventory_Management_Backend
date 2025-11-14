package com.monaum.Rapid_Global.module.master.transectionCategory;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 14-Nov-25 10:24 AM
 */

@Mapper(componentModel = "spring")
public interface TransectionCategoryMapper {

    TransectionCategory toEntity(TransectionCategoryReqDto dto);

    TransectionCategoryResDto toDto(TransectionCategory entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(TransectionCategoryReqDto dto, @MappingTarget TransectionCategory entity);
}
