package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.ResPaymentMethodDTO;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryReqDto;
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

    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status) {

        List<Unit> units = repo.findAllByActive(status);
        List<UnitResDto> unitResDtos = units.stream().map(mapper::toDTO).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", unitResDtos);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(UnitReqDto dto){

        Unit entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData("Created Successfully!",mapper.toDTO(entity));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, UnitReqDto dto) throws CustomException {
        Unit unit = repo.findById(id).orElseThrow(() -> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        mapper.toEntityUpdate(dto, unit);
        repo.save(unit);

        return ResponseUtils.SuccessResponseWithData("Updated Successfully!",mapper.toDTO(unit));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException{
        Unit unit = repo.findById(id).orElseThrow(()-> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(unit));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        Unit unit = repo.findById(id).orElseThrow(() -> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        unit.setActive(!Boolean.TRUE.equals(unit.getActive()));
        repo.save(unit);

        return ResponseUtils.SuccessResponseWithData("Updated Successfully!",mapper.toDTO(unit));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Unit unit = repo.findById(id).orElseThrow(() -> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        repo.delete(unit);

        return ResponseUtils.SuccessResponse("Unit has been deleted successfully", HttpStatus.OK);
    }

}
