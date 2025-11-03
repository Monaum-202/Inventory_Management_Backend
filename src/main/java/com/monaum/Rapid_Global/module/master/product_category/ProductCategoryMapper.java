package com.monaum.Rapid_Global.module.master.product_category;

import com.monaum.Rapid_Global.module.expenses.supplier.Supplier;
import com.monaum.Rapid_Global.module.expenses.supplier.SupplierResDto;
import com.monaum.Rapid_Global.module.expenses.supplier.UpdateSupplierReqDto;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.ResPaymentMethodDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategory toEntity(CreateProductCategoryReqDto dto);

    ProductCategoryResDto toDTO(ProductCategory entity);

    void toEntity(UpdateProductCategoryReqDto dto, @MappingTarget ProductCategory productCategory);

    ProductCategoryResDto toDto(ProductCategory productCategory);
}
