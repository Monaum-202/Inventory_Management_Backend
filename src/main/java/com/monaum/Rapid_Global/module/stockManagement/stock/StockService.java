package com.monaum.Rapid_Global.module.stockManagement.stock;

import com.monaum.Rapid_Global.enums.StockType;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.expenses.purchase.Purchase;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItem;
import com.monaum.Rapid_Global.module.master.product.Product;
import com.monaum.Rapid_Global.module.master.product.ProductRepo;
import com.monaum.Rapid_Global.module.master.product.ProductResDto;
import com.monaum.Rapid_Global.module.stockManagement.stockTransaction.StockTransaction;
import com.monaum.Rapid_Global.module.stockManagement.stockTransaction.StockTransactionRepo;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 11:39 AM
 */
@Service
@Transactional
@Slf4j
public class StockService {

    @Autowired private StockRepo stockRepo;
    @Autowired private StockTransactionRepo transactionRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private StockMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(){
        List<Stock>  stocks = stockRepo.findAll();
        List<StockDTO> stockDTOS = stocks.stream().map(mapper::toDto).toList();
        return ResponseUtils.SuccessResponseWithData(stockDTOS);
    }
    
    // Add stock when purchase is completed
    public void addFromPurchase(Purchase purchase) {
        for (PurchaseItem item : purchase.getItems()) {
            Product product = productRepo.findByName(item.getItemName())
                .orElseThrow(() -> new CustomException("Product not found: " + item.getItemName(), HttpStatus.NOT_FOUND));
            
            addStock(
                product,
                new BigDecimal(item.getQuantity()),
                item.getUnitPrice(),
                "PURCHASE",
                purchase.getId(),
                "Purchase: " + purchase.getInvoiceNo()
            );
        }
    }
    
    // Generic add stock method
    public void addStock(Product product, BigDecimal qty, BigDecimal cost,
                         String refType, Long refId, String note) {
        Stock stock = getOrCreateStock(product);
        
        // Calculate new average cost (Weighted Average)
        BigDecimal oldValue = stock.getQuantity().multiply(stock.getAverageCost());
        BigDecimal newValue = qty.multiply(cost);
        BigDecimal newQty = stock.getQuantity().add(qty);
        BigDecimal newAvgCost = oldValue.add(newValue)
            .divide(newQty, 2, RoundingMode.HALF_UP);
        
        // Update stock
        stock.setQuantity(newQty);
        stock.setAverageCost(newAvgCost);
        stockRepo.save(stock);
        
        // Record transaction
        saveTransaction(product, StockType.IN, qty, cost, newQty, refType, refId, note);
        
        log.info("Stock added - Product: {}, Qty: {}", product.getName(), qty);
    }
    
    // Remove stock (for sales)
    public void removeStock(Product product, BigDecimal qty, 
                           String refType, Long refId, String note) {
        Stock stock = stockRepo.findByProductId(product.getId())
            .orElseThrow(() -> new CustomException("Stock not found", HttpStatus.NOT_FOUND));
        
        if (stock.getQuantity().compareTo(qty) < 0) {
            throw new IllegalStateException("Insufficient stock! Available: " + stock.getQuantity());
        }
        
        BigDecimal newQty = stock.getQuantity().subtract(qty);
        stock.setQuantity(newQty);
        stockRepo.save(stock);
        
        // Record transaction (negative quantity)
        saveTransaction(product, StockType.OUT, qty.negate(),
                       stock.getAverageCost(), newQty, refType, refId, note);
        
        log.info("Stock removed - Product: {}, Qty: {}", product.getName(), qty);
    }
    
    // Adjust stock (manual correction)
    public void adjustStock(Long productId, BigDecimal newQty, String reason) {
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new CustomException("Product not found", HttpStatus.NOT_FOUND));
        
        Stock stock = getOrCreateStock(product);
        BigDecimal difference = newQty.subtract(stock.getQuantity());
        
        stock.setQuantity(newQty);
        stockRepo.save(stock);
        
        saveTransaction(product, StockType.ADJUST, difference,
                       stock.getAverageCost(), newQty, "ADJUSTMENT", null, reason);
        
        log.info("Stock adjusted - Product: {}, New Qty: {}", product.getName(), newQty);
    }
    
    // Get current stock
    public BigDecimal getCurrentStock(Long productId) {
        return stockRepo.findByProductId(productId)
            .map(Stock::getQuantity)
            .orElse(BigDecimal.ZERO);
    }
    
    // Check if stock is low
    public boolean isLowStock(Long productId) {
        Stock stock = stockRepo.findByProductId(productId).orElse(null);
        if (stock == null || stock.getAlertQuantity() == null) {
            return false;
        }
        return stock.getQuantity().compareTo(stock.getAlertQuantity()) <= 0;
    }
    
    // Get all low stock items
    public List<Stock> getLowStockItems() {
        return stockRepo.findAll().stream()
            .filter(s -> s.getAlertQuantity() != null && 
                        s.getQuantity().compareTo(s.getAlertQuantity()) <= 0)
            .collect(Collectors.toList());
    }
    
    // Get transaction history
    public List<StockTransaction> getHistory(Long productId, LocalDate from, LocalDate to) {
        return transactionRepo.findByProductIdAndDateBetween(productId, from, to);
    }
    
    // Get stock value
    public BigDecimal getStockValue(Long productId) {
        return stockRepo.findByProductId(productId)
            .map(s -> s.getQuantity().multiply(s.getAverageCost()))
            .orElse(BigDecimal.ZERO);
    }
    
    // Get total inventory value
    public BigDecimal getTotalInventoryValue() {
        return stockRepo.findAll().stream()
            .map(s -> s.getQuantity().multiply(s.getAverageCost()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Private helper methods
    private Stock getOrCreateStock(Product product) {
        return stockRepo.findByProductId(product.getId())
            .orElseGet(() -> {
                Stock newStock = new Stock();
                newStock.setProduct(product);
                newStock.setQuantity(BigDecimal.ZERO);
                newStock.setAverageCost(BigDecimal.ZERO);
                return stockRepo.save(newStock);
            });
    }
    
    private void saveTransaction(Product product, StockType type,
                                BigDecimal qty, BigDecimal cost, BigDecimal balance,
                                String refType, Long refId, String note) {
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setType(type);
        transaction.setQuantity(qty);
        transaction.setUnitCost(cost);
        transaction.setBalance(balance);
        transaction.setDate(LocalDate.now());
        transaction.setReferenceType(refType);
        transaction.setReferenceId(refId);
        transaction.setNote(note);
        transactionRepo.save(transaction);
    }
}