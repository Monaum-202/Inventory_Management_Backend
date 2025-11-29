package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryResDto;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    @Autowired private UnitRepo repo;
    @Autowired private UnitMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search) {
        List<Unit> units;

        if (search != null && !search.isBlank()) {
            units = repo.search(search);
        } else {
            units = repo.findAll();
        }

        List<UnitResDto> unitResDtos = units.stream().map(mapper::toDTO).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", unitResDtos);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(UnitReqDto dto){

        Unit entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException{
        Unit unit = repo.findById(id).orElseThrow(()-> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(unit));
    }

}
