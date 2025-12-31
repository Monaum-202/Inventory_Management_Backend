package com.monaum.Rapid_Global.module.production.bom;

import com.monaum.Rapid_Global.enums.BOMStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BOMRepository extends JpaRepository<BillOfMaterials, Long> {
    
    Optional<BillOfMaterials> findByBomCode(String bomCode);
    
    List<BillOfMaterials> findByFinishedProductId(Long productId);
    
    Optional<BillOfMaterials> findByFinishedProductIdAndIsDefaultTrue(Long productId);
    
    Page<BillOfMaterials> findByIsActive(Boolean isActive, Pageable pageable);
    
    @Query("SELECT b FROM BillOfMaterials b WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(b.bomCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.bomName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.finishedProduct.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BillOfMaterials> searchBOMs(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT b FROM BillOfMaterials b WHERE b.status = :status")
    Page<BillOfMaterials> findByStatus(@Param("status") BOMStatus status, Pageable pageable);
}