package com.monaum.Rapid_Global.module.incomes.estimate;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.incomes.customer.Customer;
import com.monaum.Rapid_Global.module.incomes.customer.CustomerRepo;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.incomes.sales.SalesRepo;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class EstimateService {

    @Autowired private EstimateRepo estimateRepository;
    @Autowired private EstimateMapper estimateMapper;
    @Autowired private EstimateItemMapper estimateItemMapper;
    @Autowired private CustomerRepo customerRepo;
    @Autowired private SalesRepo salesRepository;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(EstimateReqDTO dto) {

        // 1. Convert DTO → Entity
        Estimate estimate = estimateMapper.toEntity(dto);
        estimate.setEstimateNo(generateEstimateNo());

        // 2. Set back-reference for items
        for (EstimateItem item : estimate.getItems()) {
            item.setEstimate(estimate);
        }

        // 3. Create or update Customer
        Customer customer = customerRepo.findByPhone(dto.getPhone())
                .orElse(new Customer());

        customer.setName(dto.getCustomerName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPhone(dto.getPhone());
        customer.setCompanyName(dto.getCompanyName());

        customerRepo.save(customer);

        estimate.setCustomerId(customer.getId());

        Estimate savedEstimate = estimateRepository.save(estimate);

        return ResponseUtils.SuccessResponseWithData(
                estimateMapper.toResDto(savedEstimate)
        );
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, EstimateReqDTO dto) {

        Estimate existing = estimateRepository.findById(id)
                .orElseThrow(() -> new CustomException("Estimate not found", HttpStatus.NOT_FOUND));

        estimateMapper.updateEntityFromDto(dto, existing);

        existing.getItems().clear();

        dto.getItems().forEach(itemDto -> {
            EstimateItem item = estimateItemMapper.toEntity(itemDto);
            item.setEstimate(existing);
            existing.getItems().add(item);
        });

        Estimate updated = estimateRepository.save(existing);

        return ResponseUtils.SuccessResponseWithData(estimateMapper.toResDto(updated));
    }

    @Transactional(readOnly = true)
    public EstimateResDto getById(Long id) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found"));
        return estimateMapper.toResDto(estimate);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable) {

        Page<EstimateResDto> estimates = estimateRepository.findAll(pageable)
                .map(estimateMapper::toResDto);

        CustomPageResponseDTO<EstimateResDto> paginatedResponse =
                PaginationUtil.buildPageResponse(estimates, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public void delete(Long id) {
        if (!estimateRepository.existsById(id)) {
            throw new EntityNotFoundException("Estimate not found");
        }
        estimateRepository.deleteById(id);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> convertToSale(Long estimateId, ConvertToSaleDTO dto) {

        // 1. Get the estimate
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException("Estimate not found with id: " + estimateId, HttpStatus.NOT_FOUND));

        // 2. Check if already converted
        if (Boolean.TRUE.equals(estimate.getConvertedToSale())) {
            throw new CustomException("This estimate has already been converted to sale", HttpStatus.BAD_REQUEST);
        }

        // 3. Check if estimate is approved
        if (estimate.getStatus() != OrderStatus.COMPLETED) {
            throw new CustomException("Only approved estimates can be converted to sales", HttpStatus.BAD_REQUEST);
        }

        // 4. Create new Sales entity
        Sales sales = new Sales();
        sales.setInvoiceNo(generateInvoiceNo());
        sales.setCustomerName(estimate.getCustomerName());
        sales.setCustomerId(estimate.getCustomerId());
        sales.setPhone(estimate.getPhone());
        sales.setEmail(estimate.getEmail());
        sales.setAddress(estimate.getAddress());
        sales.setCompanyName(estimate.getCompanyName());

        // Use provided dates or defaults
        sales.setSellDate(dto.getSellDate() != null ? dto.getSellDate() : LocalDate.now());
        sales.setDeliveryDate(dto.getDeliveryDate() != null ? dto.getDeliveryDate() : estimate.getExpiryDate());

        sales.setNotes(dto.getNotes() != null ? dto.getNotes() : estimate.getNotes());
        sales.setDiscount(estimate.getDiscount());
        sales.setVat(estimate.getVat());
        sales.setStatus(OrderStatus.PENDING);

        // 5. Convert estimate items to sales items
        for (EstimateItem estimateItem : estimate.getItems()) {
            SalesItem salesItem = new SalesItem();
            salesItem.setItemName(estimateItem.getItemName());
            salesItem.setQuantity(estimateItem.getQuantity());
            salesItem.setUnitPrice(estimateItem.getUnitPrice());
            salesItem.setTotalPrice(estimateItem.getTotalPrice());
            salesItem.setSales(sales);

            sales.getItems().add(salesItem);
        }

        // 6. Save the sales
        Sales savedSales = salesRepository.save(sales);

        // 7. Update estimate to mark as converted
        estimate.setConvertedToSale(true);
        estimate.setSaleId(savedSales.getId());
        estimateRepository.save(estimate);

        return ResponseUtils.SuccessResponseWithData(savedSales.getId());
    }

    @Transactional
    public String generateEstimateNo() {
        String lastId = estimateRepository.findLastEstimateNoForUpdate();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);  // YY
        String month = String.format("%02d", LocalDate.now().getMonthValue()); // MM

        // If no previous ID → start with 001
        if (lastId == null) {
            return "EST" + year + month + "001";
        }

        // Extract last ID's year and month
        String lastYear = lastId.substring(3, 5);  // YY from ESTYYMM###
        String lastMonth = lastId.substring(5, 7); // MM from ESTYYMM###

        // If month OR year changed → reset counter to 001
        if (!lastYear.equals(year) || !lastMonth.equals(month)) {
            return "EST" + year + month + "001";
        }

        // Otherwise, increment existing number
        int number = Integer.parseInt(lastId.substring(7)); // last 3 digits
        number++;

        return "EST" + year + month + String.format("%03d", number);
    }

    @Transactional
    public String generateInvoiceNo() {
        String lastId = salesRepository.findLastInvoiceNoForUpdate();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);  // YY
        String month = String.format("%02d", LocalDate.now().getMonthValue()); // MM

        // If no previous ID → start with 001
        if (lastId == null) {
            return "INV" + year + month + "001";
        }

        // Extract last ID's year and month
        String lastYear = lastId.substring(3, 5);  // YY from INVYYMM###
        String lastMonth = lastId.substring(5, 7); // MM from INVYYMM###

        // If month OR year changed → reset counter to 001
        if (!lastYear.equals(year) || !lastMonth.equals(month)) {
            return "INV" + year + month + "001";
        }

        // Otherwise, increment existing number
        int number = Integer.parseInt(lastId.substring(7)); // last 3 digits
        number++;

        return "INV" + year + month + String.format("%03d", number);
    }
}