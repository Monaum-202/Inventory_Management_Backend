package com.monaum.Rapid_Global.module.incomes.sales;


import com.monaum.Rapid_Global.module.incomes.income.IncomeResDto;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    @Autowired
    private final SalesRepo salesRepository;
    @Autowired
    private final SalesMapper salesMapper;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(SalesReqDTO dto) {

        Sales sales = salesMapper.toEntity(dto);

        for (SalesItem item : sales.getItems()) {
            item.setSales(sales);
        }

        Sales saved = salesRepository.save(sales);

        return  ResponseUtils.SuccessResponseWithData(salesMapper.toResDto(saved));
    }


    public SalesResDto update(Long id, SalesReqDTO dto) {
        Sales existing = salesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales not found"));

        // Clear old items (because orphanRemoval=true)
        existing.getItems().clear();

        // Map new values
        Sales updated = salesMapper.toEntity(dto);

        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setCreatedBy(existing.getCreatedBy());

        // Re-link items
        for (SalesItem item : updated.getItems()) {
            item.setSales(updated);
        }

        Sales saved = salesRepository.save(updated);
        return salesMapper.toResDto(saved);
    }


    @Transactional(readOnly = true)
    public SalesResDto getById(Long id) {
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales not found"));
        return salesMapper.toResDto(sales);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search,Pageable pageable) {

        Page<SalesResDto> sales = salesRepository.findAll(pageable).map(salesMapper::toResDto);

        CustomPageResponseDTO<SalesResDto> paginatedResponse = PaginationUtil.buildPageResponse(sales, pageable);
        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public void delete(Long id) {
        if (!salesRepository.existsById(id)) {
            throw new EntityNotFoundException("Sales not found");
        }
        salesRepository.deleteById(id);
    }
}
