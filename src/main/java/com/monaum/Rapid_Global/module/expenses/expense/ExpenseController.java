package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 08-Nov-25 11:04 PM
 */

@RestApiController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired ExpenseService service;

    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody ExpenseReqDTO dto
    ){
        return   service.create(dto);
    }
}
