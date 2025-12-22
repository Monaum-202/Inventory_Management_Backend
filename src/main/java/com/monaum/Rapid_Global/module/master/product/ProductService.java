package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.enums.ProductType;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.product_log.ProductLog;
import com.monaum.Rapid_Global.module.master.product_log.ProductLogRepo;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitRepo;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import com.monaum.Rapid_Global.module.stockManagement.stock.Stock;
import com.monaum.Rapid_Global.module.stockManagement.stock.StockRepo;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired private StockRepo stockRepo;

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

//    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status) {
//
//        List<Product> products = repo.findAllByActive(status);
//        List<ProductResDto> productResDtos = products.stream().map(mapper::toDto).toList();
//
//        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", productResDtos);
//    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Boolean status, ProductType type) {
        List<Product> products = repo.searchAndFilter((search != null && !search.isBlank()) ? search : null, status,type);

        List<ProductResDto> productResDtos = products.stream().map(mapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", productResDtos);
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ProductReqDto dto) {

        Unit unit = unitRepo.findById(dto.getUnitId())
                .orElseThrow(() -> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        Product product = mapper.toEntity(dto);
        product.setUnit(unit);
        product = repo.save(product);

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(BigDecimal.ZERO);
        stock.setAlertQuantity(dto.getAlertQuantity());
        stock.setAverageCost(BigDecimal.ZERO);

        stockRepo.save(stock);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(product));
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ProductReqDto dto) {

        Product product = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Unit newUnit = unitRepo.findById(dto.getUnitId()).orElseThrow(() -> new EntityNotFoundException("Unit not found"));

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

        mapper.updateEntity(product, dto);
        product.setUnit(newUnit);

        product = repo.save(product);

        Stock stock = stockRepo.findByProduct(product)
                .orElseThrow(() -> new CustomException("Stock not found",HttpStatus.NOT_FOUND));

        stock.setAlertQuantity(dto.getAlertQuantity());
        stockRepo.save(stock);

        log.setNewName(product.getName());
        log.setNewDescription(product.getDescription());
        log.setNewUnitId(product.getUnit() != null ? product.getUnit().getId() : null);
        log.setNewPricePerUnit(product.getPricePerUnit());
        log.setNewStatus(product.getActive() != null && product.getActive() ? 1 : 0);

        productLogRepo.save(log);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(product));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        Product product = repo.findById(id).orElseThrow(() -> new CustomException("Unit not found", HttpStatus.NOT_FOUND));

        product.setActive(!Boolean.TRUE.equals(product.getActive()));
        repo.save(product);

        return ResponseUtils.SuccessResponseWithData(mapper.toDto(product));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) {
        Product product = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        repo.delete(product);

        return ResponseUtils.SuccessResponse("Product deleted successfully");
    }

}
