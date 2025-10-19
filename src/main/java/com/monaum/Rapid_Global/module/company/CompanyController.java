package com.monaum.Rapid_Global.module.company;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.ResponseStatusType;
import com.monaum.Rapid_Global.model.ResponseBuilder;
import com.monaum.Rapid_Global.model.SuccessResponse;

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

    @Autowired private CompanyService transactionService;

    @GetMapping
    public ResponseEntity<?> getCompanys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CompanyResDto> result = transactionService.getAllByUser(pageable);

        return ResponseBuilder.build(ResponseStatusType.READ_SUCCESS, result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<CompanyResDto>> getCompanyById(@PathVariable Long id) {
        CompanyResDto resData = transactionService.getById(id);
        return ResponseBuilder.build(ResponseStatusType.READ_SUCCESS, resData);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<CompanyResDto>> create(@Valid @RequestBody CreateCompanyReqDto reqDto) {
        CompanyResDto resData = transactionService.create(reqDto);
        return ResponseBuilder.build(ResponseStatusType.CREATE_SUCCESS, resData);
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<CompanyResDto>> update(@Valid @RequestBody UpdateCompanyReqDto dto) {
        CompanyResDto resData = transactionService.update(dto);
        return ResponseBuilder.build(ResponseStatusType.UPDATE_SUCCESS, resData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseBuilder.build(ResponseStatusType.DELETE_SUCCESS, null);
    }

//    @GetMapping
//    public ResponseEntity<SuccessResponse<List<CompanyResDto>>> getAllCompany() {
//        List<CompanyResDto> resData = transactionService.getAllTags();
//        return ResponseBuilder.build(ResponseStatusType.READ_SUCCESS, resData);
//    }
}
