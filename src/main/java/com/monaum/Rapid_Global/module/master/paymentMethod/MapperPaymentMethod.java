package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Mapper(componentModel = "spring")
public interface MapperPaymentMethod {

    MapperPaymentMethod INSTANCE = Mappers.getMapper(MapperPaymentMethod.class);

    PaymentMethod toEntity(ReqPaymentMethodDTO dto);

    ResPaymentMethodDTO toDTO(PaymentMethod entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(ReqPaymentMethodDTO dto, @MappingTarget PaymentMethod entity);
}
