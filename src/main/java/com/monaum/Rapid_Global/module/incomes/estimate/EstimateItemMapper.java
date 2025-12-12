package com.monaum.Rapid_Global.module.incomes.estimate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Mapper(componentModel = "spring")
public interface EstimateItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estimate", ignore = true)
    EstimateItem toEntity(EstimateItemReqDto dto);

    EstimateItemResDto toResDto(EstimateItem entity);
}