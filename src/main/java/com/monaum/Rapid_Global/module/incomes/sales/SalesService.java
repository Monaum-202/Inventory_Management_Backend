package com.monaum.Rapid_Global.module.incomes.sales;


import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.incomes.customer.Customer;
import com.monaum.Rapid_Global.module.incomes.customer.CustomerRepo;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.income.IncomeRepo;
import com.monaum.Rapid_Global.module.incomes.income.IncomeResDto;
import com.monaum.Rapid_Global.module.incomes.income.IncomeService;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
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
    @Autowired private CustomerRepo customerRepo;
    @Autowired private IncomeRepo incomeRepo;
    @Autowired private IncomeService  incomeService;
    @Autowired private RepoPaymentMethod paymentMethodRepo;
    @Autowired private TransactionCategoryRepo transactionCategoryRepo;

//    @Transactional
//    public ResponseEntity<BaseApiResponseDTO<?>> create(SalesReqDTO dto) {
//
//        Sales sales = salesMapper.toEntity(dto);
//        sales.setInvoiceNo(generateInvoiceNo());
//
//        Customer customer =  new Customer();
//        customer.setName(dto.getCustomerName());
//        customer.setEmail(dto.getEmail());
//        customer.setAddress(dto.getAddress());
//        customer.setPhone(dto.getPhone());
//        customer.setBusinessName(dto.getCompanyName());
//
//        for (SalesItem item : sales.getItems()) {
//            item.setSales(sales);
//        }
//
//        customerRepo.save(customer);
//
//        return  ResponseUtils.SuccessResponseWithData(salesMapper.toResDto(salesRepository.save(sales)));
//    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(SalesReqDTO dto) {

        // 1. Fetch payment method
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId())
                .orElseThrow(() -> new CustomException(
                        "Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));

        // 2. Map DTO to Sales entity
        Sales sales = salesMapper.toEntity(dto);
        sales.setInvoiceNo(generateInvoiceNo());

        // Initialize payments list to avoid null issues
        if (sales.getPayments() == null) {
            sales.setPayments(new ArrayList<>());
        }

        // Set sale reference for each child item
        for (SalesItem item : sales.getItems()) {
            item.setSales(sales);
        }

        // 3. Fetch or create customer
        Customer customer = customerRepo.findByPhone(dto.getPhone())
                .orElse(new Customer());

        customer.setName(dto.getCustomerName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPhone(dto.getPhone());
        customer.setCompanyName(dto.getCompanyName());

        customerRepo.save(customer);

        // 4. Save Sales first
        Sales savedSales = salesRepository.save(sales);

            Income income = new Income();
            income.setIncomeId(incomeService.generateIncomeId());
            income.setAmount(savedSales.getPaidAmount());
            income.setIncomeDate(LocalDate.now());
            income.setDescription("Income from Sales Invoice: " + savedSales.getInvoiceNo());
            income.setSales(savedSales);
            income.setPaymentMethod(paymentMethod);

            // Optionally assign a default category if required
            TransactionCategory defaultCategory = transactionCategoryRepo.findByName("Sales").orElse(null);
            income.setIncomeCategory(defaultCategory);

            income.setStatus(Status.APPROVED);
            income.setApprovedAt(LocalDateTime.now());

            incomeRepo.save(income);

            // Add income to Sales payments list
            savedSales.getPayments().add(income);
            salesRepository.save(savedSales); // update sales with payment


        // 6. Return response
        return ResponseUtils.SuccessResponseWithData(salesMapper.toResDto(savedSales));
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
