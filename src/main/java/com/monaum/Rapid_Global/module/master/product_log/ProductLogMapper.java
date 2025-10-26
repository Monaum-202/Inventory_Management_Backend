package com.monaum.Rapid_Global.module.master.product_log;

import com.monaum.Rapid_Global.module.master.product_log.*;
        import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductLogMapper {

    // DTO → Entity (Create)
    @Mapping(source = "productId", target = "product.id")
    ProductLog toEntity(CreateProductLogReqDto dto);

    // DTO → Entity (Update)
    @Mapping(source = "productId", target = "product.id")
    void toEntity(UpdateProductLogReqDto dto, @MappingTarget ProductLog productLog);

    // Entity → DTO (Response)
    @Mapping(source = "product.id", target = "productId")
    ProductLogDto toDto(ProductLog productLog);
}

