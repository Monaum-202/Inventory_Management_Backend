package com.monaum.Rapid_Global.module.master.company;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.ResponseStatusType;
import com.monaum.Rapid_Global.model.ResponseBuilder;
import com.monaum.Rapid_Global.model.SuccessResponse;

import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@RestApiController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired private CompanyService companyService;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return companyService.getAllByUser(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return companyService.getById(id);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody CreateCompanyReqDto reqDto
    ){
        return companyService.create(reqDto);
    }

    @PutMapping
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @Valid @RequestBody UpdateCompanyReqDto dto
    ){
        return companyService.update(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> delete(
            @PathVariable Long id
    ){
        return companyService.delete(id);
    }

//    @GetMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> getAllCompany() {
//        List<CompanyResDto> resData = transactionService.getAllTags();
//        return ResponseBuilder.build(ResponseStatusType.READ_SUCCESS, resData);
//    }
}
