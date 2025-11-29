package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.exception.CustomException;
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

    public ResponseEntity<BaseApiResponseDTO<?>> getAll() {
        List<Unit> units = repo.findAll();
        List<UnitResDto> unitDtos = units.stream().map(mapper::toDTO).toList();

        BaseApiResponseDTO<List<UnitResDto>> response = new BaseApiResponseDTO<>(true, "Units fetched successfully", unitDtos);

        return ResponseUtils.SuccessResponseWithData(response);
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
