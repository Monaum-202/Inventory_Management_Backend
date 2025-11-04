package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.module.master.product_category.ProductCategory;
import com.monaum.Rapid_Global.module.master.product_category.ProductCategoryReqDto;
import com.monaum.Rapid_Global.module.master.product_category.RepoProductCategory;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UnitService {

    @Autowired private UnitRepo repo;

    @Autowired private UnitMapper mapper;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(UnitReqDto dto){

        Unit entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

}
