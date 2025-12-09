package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.module.master.product_log.ProductLog;
import com.monaum.Rapid_Global.module.master.product_log.ProductLogRepo;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitRepo;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 09-Dec-25 8:56 PM
 */

@Service
public class ProductService {
    @Autowired private ProductRepo repo;
    @Autowired private ProductMapper mapper;
    @Autowired private UnitRepo unitRepo;
    @Autowired private SecurityUtil  securityUtil;
    @Autowired private ProductLogRepo productLogRepo;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search){
        List<Product> products;

        if (search != null && !search.isBlank()) {
            products = repo.search(search);
        } else {
            products = repo.findAll();
        }

        List<ProductResDto> productResDtos = products.stream().map(mapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", productResDtos);

    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ProductReqDto dto) {
        Unit unit = unitRepo.findById(dto.getUnitId()).orElseThrow(()->new EntityNotFoundException("Unit not found"));

        Product product = mapper.toEntity(dto);
        product.setUnit(unit);
        product = repo.save(product);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(product));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ProductReqDto dto) {

        // ----- 2. Existing product -----
        Product product = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // ----- 3. New Unit -----
        Unit newUnit = unitRepo.findById(dto.getUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));

        // ----- 4. Build ProductLog (old values first) -----
        ProductLog log = ProductLog.builder()
                .product(product)
                .changedBy(securityUtil.getAuthenticatedUser().getId())

                .oldName(product.getName())
                .oldDescription(product.getDescription())
                .oldUnitId(product.getUnit() != null ? product.getUnit().getId() : null)
                .oldPricePerUnit(product.getPricePerUnit())
                .oldStatus(product.getActive() != null && product.getActive() ? 1 : 0)

                .changedAt(LocalDateTime.now())
                .build();

        // ----- 5. Update product fields using MapStruct -----
        mapper.updateEntity(product, dto);
        product.setUnit(newUnit);

        // ----- 6. Save updated product -----
        product = repo.save(product);

        // ----- 7. Set NEW values inside log -----
        log.setNewName(product.getName());
        log.setNewDescription(product.getDescription());
        log.setNewUnitId(product.getUnit() != null ? product.getUnit().getId() : null);
        log.setNewPricePerUnit(product.getPricePerUnit());
        log.setNewStatus(product.getActive() != null && product.getActive() ? 1 : 0);

        // ----- 8. Save ProductLog -----
        productLogRepo.save(log);

        // ----- 9. Return updated Product DTO -----
        return ResponseUtils.SuccessResponseWithData(mapper.toDto(product));
    }

}
