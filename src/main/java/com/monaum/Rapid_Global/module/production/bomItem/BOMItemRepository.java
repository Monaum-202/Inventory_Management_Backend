package com.monaum.Rapid_Global.module.production.bomItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BOMItemRepository extends JpaRepository<BOMItem, Long> {
    
    List<BOMItem> findByBomId(Long bomId);
    
    List<BOMItem> findByRawMaterialId(Long rawMaterialId);
    
    @Query("SELECT bi FROM BOMItem bi WHERE bi.bom.id = :bomId ORDER BY bi.sequenceOrder")
    List<BOMItem> findByBomIdOrderBySequence(@Param("bomId") Long bomId);
}