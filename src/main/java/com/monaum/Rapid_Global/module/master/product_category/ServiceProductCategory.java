package com.monaum.Rapid_Global.module.master.product_category;


import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServiceProductCategory {
    @Autowired private RepoProductCategory repo;
    @Autowired private ProductCategoryMapper mapper;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ProductCategoryReqDto dto){

        ProductCategory entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }


}
