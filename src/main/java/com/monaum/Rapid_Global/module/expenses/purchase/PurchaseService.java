package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseReqDTO;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseService;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItem;
import com.monaum.Rapid_Global.module.expenses.supplier.Supplier;
import com.monaum.Rapid_Global.module.expenses.supplier.SupplierRepo;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.RepoPaymentMethod;
import com.monaum.Rapid_Global.module.master.product.Product;
import com.monaum.Rapid_Global.module.master.product.ProductRepo;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryRepo;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.stockManagement.stock.StockRepo;
import com.monaum.Rapid_Global.module.stockManagement.stock.StockService;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 12:32 AM
 */
@Service
@Slf4j
public class PurchaseService {

    @Autowired public PurchaseRepo repo;
    @Autowired public PurchaseMapper mapper;
    @Autowired public SupplierRepo supplierRepo;
    @Autowired public ExpenseService expenseService;
    @Autowired public TransactionCategoryRepo transactionCategoryRepo;
    @Autowired private RepoPaymentMethod paymentMethodRepo;
    @Autowired public ProductRepo productRepo;
    @Autowired public StockService stockService;



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

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(PurchaseReqDTO dto) {

        // 1. Convert DTO → Entity
        Purchase purchase = mapper.toEntity(dto);
        purchase.setInvoiceNo(generateInvoiceNo());
        purchase.setStatus(OrderStatus.PENDING);

        // 2. Set back-reference for items
        for (PurchaseItem item : purchase.getItems()) {
            item.setPurchase(purchase);
        }

        // 3. Create or update Customer
        Supplier supplier = supplierRepo.findByPhone(dto.getPhone())
                .orElse(new Supplier());

        supplier.setName(dto.getSupplierName());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setPhone(dto.getPhone());
        supplier.setCompanyName(dto.getCompanyName());

        supplierRepo.save(supplier);

        // 4. Prepare Income (payments)
        List<Expense> expenseList = new ArrayList<>();

        if (dto.getPayments() != null && !dto.getPayments().isEmpty()) {
            for (ExpenseReqDTO req : dto.getPayments()) {

                // Fetch Payment Method correctly
                PaymentMethod paymentMethod = paymentMethodRepo.findById(req.getPaymentMethodId())
                        .orElseThrow(() -> new RuntimeException("Payment method not found"));

                // Fetch category "sales"
                TransactionCategory category =
                        transactionCategoryRepo.findByNameIgnoreCase("purchase")
                                .orElse(null);

                Expense expense = new Expense();
                expense.setExpenseId(expenseService.generateExpenseId());
                expense.setAmount(req.getAmount());
                expense.setExpenseDate(req.getExpenseDate());
                expense.setDescription(req.getDescription());
                expense.setPaymentMethod(paymentMethod);
                expense.setExpenseCategory(category);
                expense.setPurchase(purchase);
                expense.setPaidTo(supplier.getName());
                expense.setPaidToId(supplier.getId());

                expense.setStatus(Status.APPROVED);
                expense.setApprovedAt(LocalDateTime.now());

                expenseList.add(expense);
            }
        }

        purchase.setSupplierId(supplier.getId());
        purchase.setPayments(expenseList);

        Purchase save = repo.save(purchase);

        try {
            addStockFromPurchase(purchase);
            log.info("Purchase completed and stock added: {}", purchase.getInvoiceNo());
        } catch (Exception e) {
            log.error("Failed to add stock for purchase: {}", purchase.getInvoiceNo(), e);
            throw new RuntimeException("Failed to update stock: " + e.getMessage());
        }

        return ResponseUtils.SuccessResponseWithData("Sales created successfully!", mapper.toResDto(save));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> completePurchase(Long purchaseId) {

        Purchase purchase = repo.findById(purchaseId)
                .orElseThrow(() -> new CustomException("Purchase not found", HttpStatus.NOT_FOUND));

        if (purchase.getStatus() == OrderStatus.COMPLETED) {
            throw new CustomException("Purchase already completed", HttpStatus.ALREADY_REPORTED);
        }

        if (purchase.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException("Cannot complete cancelled purchase", HttpStatus.NOT_ACCEPTABLE);
        }

        // Update status
        purchase.setStatus(OrderStatus.COMPLETED);
        purchase.setDeliveryDate(LocalDate.now());
        Purchase updated = repo.save(purchase);

        // **ADD STOCK** - This is where stock increases
        try {
            addStockFromPurchase(purchase);
            log.info("Purchase completed and stock added: {}", purchase.getInvoiceNo());
        } catch (Exception e) {
            log.error("Failed to add stock for purchase: {}", purchase.getInvoiceNo(), e);
            throw new RuntimeException("Failed to update stock: " + e.getMessage());
        }

        return ResponseUtils.SuccessResponseWithData(
                "Purchase completed and stock updated!",
                mapper.toResDto(updated)
        );
    }


    //Helper Methods

    private void addStockFromPurchase(Purchase purchase) {
        for (PurchaseItem item : purchase.getItems()) {

            // Find or create product
            Product product = productRepo.findByName(item.getItemName())
                    .orElseThrow(()-> new CustomException("Product Not Found", HttpStatus.NOT_FOUND));

            // Add stock
            stockService.addStock(
                    product,
                    new BigDecimal(item.getQuantity()),
                    item.getUnitPrice(),
                    "PURCHASE",
                    purchase.getId(),
                    "Purchase: " + purchase.getInvoiceNo() + " from " + purchase.getSupplierName()
            );
        }
    }

    @Transactional
    public String generateInvoiceNo() {
        String lastId = repo.findLastInvoiceNoForUpdate();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);  // YY
        String month = String.format("%02d", LocalDate.now().getMonthValue()); // MM

        // If no previous ID → start with 001
        if (lastId == null) {
            return "PUR" + year + month + "001";
        }

        // Extract last ID's year and month
        String lastYear = lastId.substring(3, 5);
        String lastMonth = lastId.substring(5, 7);

        // If month OR year changed → reset counter to 001
        if (!lastYear.equals(year) || !lastMonth.equals(month)) {
            return "PUR" + year + month + "001";
        }

        // Otherwise, increment existing number
        int number = Integer.parseInt(lastId.substring(7));
        number++;

        return "PUR" + year + month + String.format("%03d", number);
    }
}
