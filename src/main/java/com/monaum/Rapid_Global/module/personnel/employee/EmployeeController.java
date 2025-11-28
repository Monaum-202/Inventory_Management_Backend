package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@RestApiController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired private EmployeeService employeeService;

//    @GetMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return employeeService.getAll(pageable);
//    }

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable
    ) {

        return employeeService.getAllTestLend(search, pageable);
    }

    @GetMapping("/all-active")
    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
            @RequestParam Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("sqn").descending());

        return employeeService.getAllActive(active,pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return employeeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody EmployeeReqDto dto
    ){
        return employeeService.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeReqDto dto) {

        return employeeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> delete(
            @PathVariable Long id
    ){
        return employeeService.delete(id);
    }

   @PatchMapping("/{id}")
   public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(
           @PathVariable Long id
   ) {
       return employeeService.activeUpdate(id);
   }

   //for large import
   @PostMapping("/import")
   public ResponseEntity<BaseApiResponseDTO<?>> importEmployees(
           @RequestBody List<EmployeeReqDto> employeeList
   ) {
       return employeeService.importEmployees(employeeList);
   }

}
