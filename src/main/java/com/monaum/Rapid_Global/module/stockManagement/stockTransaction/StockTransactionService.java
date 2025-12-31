package com.monaum.Rapid_Global.module.stockManagement.stockTransaction;

import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 31-Dec-25 4:18 PM
 */

@Service
@AllArgsConstructor
public class StockTransactionService {

    private StockTransactionRepo stockTransactionRepo;
    private StockTransactionMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable) {
        Page<StockTransactionResDTO> stockTransactionPage = stockTransactionRepo.findAll(pageable).map(mapper::toDto);
        CustomPageResponseDTO<StockTransactionResDTO> stockTransactionDTO = PaginationUtil.buildPageResponse(stockTransactionPage,pageable);

        return ResponseUtils.SuccessResponseWithData(stockTransactionDTO);
    }
}
