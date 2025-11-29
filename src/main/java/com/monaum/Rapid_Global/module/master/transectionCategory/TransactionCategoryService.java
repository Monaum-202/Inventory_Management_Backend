package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.enums.TransactionType;
import com.monaum.Rapid_Global.exception.CustomException;
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

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 14-Nov-25 10:26 AM
 */

@Service
public class TransactionCategoryService {

    @Autowired private TransactionCategoryRepo repo;
    @Autowired private TransactionCategoryMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search) {
        List<TransactionCategory> transactionCategories;

        if (search != null && !search.isBlank()) {
            transactionCategories = repo.search(search);
        } else {
            transactionCategories = repo.findAll();
        }

        List<TransactionCategoryResDto> transactionCategoryResDtos = transactionCategories.stream().map(mapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", transactionCategoryResDtos);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status, TransactionType type) {

        List<TransactionCategory> transactionCategories = repo.findAllByActiveAndType(status, type);
        List<TransactionCategoryResDto> transactionCategoryResDtos = transactionCategories.stream().map(mapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", transactionCategoryResDtos);
    }



    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        TransactionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found at "+ id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(TransactionCategoryReqDto dto){

        TransactionCategory entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(entity));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, TransactionCategoryReqDto dto) throws CustomException {
        TransactionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        mapper.toEntityUpdate(dto, transectionCategory);
        repo.save(transectionCategory);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        TransactionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        transectionCategory.setActive(!Boolean.TRUE.equals(transectionCategory.getActive()));
        repo.save(transectionCategory);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(transectionCategory));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        TransactionCategory transectionCategory = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        repo.delete(transectionCategory);

        return ResponseUtils.SuccessResponse("Payment Method has been deleted successfully", HttpStatus.OK);
    }
}
