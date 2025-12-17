package com.monaum.Rapid_Global.module.stockManagement.stockTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockTransactionRepo extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByProductIdAndDateBetween(Long productId, LocalDate from, LocalDate to);
    List<StockTransaction> findByProductIdOrderByDateDesc(Long productId);
}