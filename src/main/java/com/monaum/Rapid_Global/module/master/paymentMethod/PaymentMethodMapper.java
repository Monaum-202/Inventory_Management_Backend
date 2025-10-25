package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    @Mapping(target = "id", ignore = true)
    PaymentMethod toEntity(PaymentMethodCreateDTO dto);

    PaymentMethodResponseDTO toDTO(PaymentMethod entity);
}
