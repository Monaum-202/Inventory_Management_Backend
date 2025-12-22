package com.monaum.Rapid_Global.module.stockManagement.stock;

import com.monaum.Rapid_Global.module.master.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductId(Long productId);
    Optional<Stock> findByProduct(Product product);
}