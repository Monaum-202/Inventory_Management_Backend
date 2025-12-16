package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 12:32 AM
 */
@Service
public class PurchaseService {

    @Autowired public PurchaseRepo repo;
    @Autowired public PurchaseMapper mapper;

    @Transactional(readOnly = true)
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable) {

        Page<PurchaseResDto> purchaseResDtos =
                (search != null && !search.trim().isEmpty())
                        ? repo.search(search.trim(), pageable).map(mapper::toResDto)
                        : repo.findAll(pageable).map(mapper::toResDto);

        CustomPageResponseDTO<PurchaseResDto> response =
                PaginationUtil.buildPageResponse(purchaseResDtos, pageable);

        return ResponseUtils.SuccessResponseWithData(response);
    }
}
