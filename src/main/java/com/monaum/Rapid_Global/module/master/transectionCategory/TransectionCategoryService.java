package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.enums.TransactionType;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.paymentMethod.ResPaymentMethodDTO;
import com.monaum.Rapid_Global.module.personnel.employee.EmployeeResDto;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 14-Nov-25 10:26 AM
 */

@Service
public class TransectionCategoryService {

    @Autowired private TransectionCategoryRepo repo;
    @Autowired private TransectionCategoryMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable) {
        Page<TransectionCategoryResDto> transectionCates;

        if (search != null && !search.isBlank()) {
            transectionCates = repo.search(search, pageable).map(mapper::toDto);
        } else {
            transectionCates = repo.findAll(pageable).map(mapper::toDto);
        }

        CustomPageResponseDTO<TransectionCategoryResDto> paginatedResponse = PaginationUtil.buildPageResponse(transectionCates, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status, TransactionType type, Pageable pageable) {

        Page<TransectionCategoryResDto> page =
                repo.findAllByActiveAndType(status, type, pageable)
                        .map(mapper::toDto);

        CustomPageResponseDTO<TransectionCategoryResDto> paginatedResponse =
                PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        TransectionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found at "+ id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(TransectionCategoryReqDto dto){

        TransectionCategory entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(entity));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, TransectionCategoryReqDto dto) throws CustomException {
        TransectionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        mapper.toEntityUpdate(dto, transectionCategory);
        repo.save(transectionCategory);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        TransectionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        transectionCategory.setActive(!Boolean.TRUE.equals(transectionCategory.getActive()));
        repo.save(transectionCategory);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        TransectionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        repo.delete(transectionCategory);

        return ResponseUtils.SuccessResponse("Payment Method has been deleted successfully", HttpStatus.OK);
    }
}
