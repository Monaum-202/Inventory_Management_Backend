package com.monaum.Rapid_Global.module.incomes.sales;


import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.model.SalesTimerService;
import com.monaum.Rapid_Global.module.incomes.customer.Customer;
import com.monaum.Rapid_Global.module.incomes.customer.CustomerRepo;
import com.monaum.Rapid_Global.module.incomes.income.*;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemMapper;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.RepoPaymentMethod;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryRepo;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    @Autowired private SalesRepo salesRepository;
    @Autowired private SalesMapper salesMapper;
    @Autowired private SalesItemMapper salesItemMapper;
    @Autowired private CustomerRepo customerRepo;
    @Autowired private SalesTimerService salesTimerService;
    @Autowired private IncomeService  incomeService;
    @Autowired private RepoPaymentMethod paymentMethodRepo;
    @Autowired private TransactionCategoryRepo transactionCategoryRepo;


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(SalesReqDTO dto) {

        // 1. Convert DTO → Entity
        Sales sales = salesMapper.toEntity(dto);
        sales.setInvoiceNo(generateInvoiceNo());

        // 2. Set back-reference for items
        for (SalesItem item : sales.getItems()) {
            item.setSales(sales);
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

        // 4. Prepare Income (payments)
        List<Income> incomeList = new ArrayList<>();

        if (dto.getPayments() != null && !dto.getPayments().isEmpty()) {
            for (IncomeReqDTO req : dto.getPayments()) {

                // Fetch Payment Method correctly
                PaymentMethod paymentMethod = paymentMethodRepo.findById(req.getPaymentMethodId())
                        .orElseThrow(() -> new RuntimeException("Payment method not found"));

                // Fetch category "sales"
                TransactionCategory category =
                        transactionCategoryRepo.findByNameIgnoreCase("sales")
                                .orElse(null);

                Income income = new Income();
                income.setIncomeId(incomeService.generateIncomeId());
                income.setAmount(req.getAmount());
                income.setIncomeDate(req.getIncomeDate());
                income.setDescription(req.getDescription());
                income.setPaymentMethod(paymentMethod);
                income.setIncomeCategory(category);
                income.setSales(sales);
                income.setPaidFrom(customer.getName());
                income.setPaidFromId(customer.getId());

                income.setStatus(Status.APPROVED);
                income.setApprovedAt(LocalDateTime.now());

                incomeList.add(income);
            }
        }

        sales.setCustomerId(customer.getId());
        sales.setPayments(incomeList);

        Sales savedSales = salesRepository.save(sales);
        salesTimerService.startProcessingTimer(savedSales.getId());

        return ResponseUtils.SuccessResponseWithData(
                salesMapper.toResDto(savedSales)
        );
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, SalesReqUpdateDTO dto) {

        Sales existing = salesRepository.findById(id)
                .orElseThrow(() -> new CustomException("Sales not found", HttpStatus.NOT_FOUND));

        salesMapper.updateEntityFromDto(dto, existing);

        existing.getItems().clear();

        dto.getItems().forEach(itemDto -> {
            SalesItem item = salesItemMapper.toEntity(itemDto);
            item.setSales(existing);
            existing.getItems().add(item);
        });

        Sales updated = salesRepository.save(existing);

        return ResponseUtils.SuccessResponseWithData(salesMapper.toResDto(updated));
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

    public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(Long id, OrderStatus status, String cancelReason) {
        Sales sales = salesRepository.findById(id).orElseThrow(() -> new CustomException("Income not found with id: " + id, HttpStatus.NOT_FOUND));

        sales.setStatus(status);
        if (cancelReason!=null){sales.setCancelReason(cancelReason);}

        salesRepository.save(sales);

        return ResponseUtils.SuccessResponse("Approved successfully!", HttpStatus.OK);
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
        String lastYear = lastId.substring(3, 5);  // YY from EXPYYMM###
        String lastMonth = lastId.substring(5, 7); // MM from EXPYYMM###

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
