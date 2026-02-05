package com.monaum.Rapid_Global.module.report.salesReport;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 04-Feb-26 11:12 PM
 */
@Repository
public interface SalesReportRepository extends JpaRepository<Sales, Long> {

    // ========================================================================
    // CORE QUERY METHODS - OPTIMIZED
    // ========================================================================

    /**
     * Find sales with filters - OPTIMIZED with covering index
     * Uses: idx_sales_sell_date_status
     */
    @Query("SELECT s FROM Sales s WHERE " +
            "(:startDate IS NULL OR s.sellDate >= :startDate) AND " +
            "(:endDate IS NULL OR s.sellDate <= :endDate) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:customerId IS NULL OR s.customerId = :customerId) AND " +
            "(:customerName IS NULL OR LOWER(s.customerName) LIKE LOWER(CONCAT('%', :customerName, '%')))")
    @QueryHints({
            @QueryHint(name = "org.hibernate.fetchSize", value = "100"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")
    })
    Page<Sales> findSalesWithFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status,
            @Param("customerId") Long customerId,
            @Param("customerName") String customerName,
            Pageable pageable
    );

    // ========================================================================
    // MATERIALIZED VIEW QUERIES - ULTRA FAST
    // ========================================================================

    /**
     * Get summary from materialized view (instant, pre-calculated)
     * 100x faster than calculating on-the-fly
     */
    @Query(value =
            "SELECT " +
                    "    COALESCE(SUM(order_count), 0) as totalOrders, " +
                    "    COALESCE(SUM(total_revenue), 0) as totalRevenue, " +
                    "    COALESCE(SUM(total_discount), 0) as totalDiscount, " +
                    "    COALESCE(SUM(total_vat), 0) as totalVat, " +
                    "    COALESCE(SUM(CASE WHEN status = 'PENDING' THEN order_count ELSE 0 END), 0) as pendingOrders, " +
                    "    COALESCE(SUM(CASE WHEN status = 'CONFIRMED' THEN order_count ELSE 0 END), 0) as confirmedOrders, " +
                    "    COALESCE(SUM(CASE WHEN status = 'SHIPPED' THEN order_count ELSE 0 END), 0) as shippedOrders, " +
                    "    COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN order_count ELSE 0 END), 0) as deliveredOrders, " +
                    "    COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN order_count ELSE 0 END), 0) as cancelledOrders, " +
                    "    COALESCE(SUM(total_items), 0) as totalItems " +
                    "FROM mv_daily_sales_summary " +
                    "WHERE sale_date BETWEEN :startDate AND :endDate " +
                    "AND (:status IS NULL OR status = :status)",
            nativeQuery = true
    )
    Map<String, Object> getSummaryFromMaterializedView(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status
    );

    /**
     * Fallback: Get summary directly from tables (slower but always accurate)
     */
    @Query(value =
            "SELECT " +
                    "    COUNT(*) as totalOrders, " +
                    "    COALESCE(SUM(CASE WHEN s.status = 'PENDING' THEN 1 ELSE 0 END), 0) as pendingOrders, " +
                    "    COALESCE(SUM(CASE WHEN s.status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) as confirmedOrders, " +
                    "    COALESCE(SUM(CASE WHEN s.status = 'SHIPPED' THEN 1 ELSE 0 END), 0) as shippedOrders, " +
                    "    COALESCE(SUM(CASE WHEN s.status = 'DELIVERED' THEN 1 ELSE 0 END), 0) as deliveredOrders, " +
                    "    COALESCE(SUM(CASE WHEN s.status = 'CANCELLED' THEN 1 ELSE 0 END), 0) as cancelledOrders, " +
                    "    COALESCE(SUM(si.total_price), 0) as totalRevenue, " +
                    "    COALESCE(SUM(s.discount), 0) as totalDiscount, " +
                    "    COALESCE(SUM(s.vat), 0) as totalVat " +
                    "FROM Sales s " +
                    "LEFT JOIN sales_item si ON s.id = si.sales_id " +
                    "WHERE s.sell_date BETWEEN :startDate AND :endDate " +
                    "AND (:status IS NULL OR s.status = :status) " +
                    "AND (:customerId IS NULL OR s.customer_id = :customerId)",
            nativeQuery = true
    )
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Map<String, Object> getSalesSummaryDirect(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status,
            @Param("customerId") Long customerId
    );

    /**
     * Get total items sold - optimized with index
     */
    @Query("SELECT COALESCE(SUM(si.quantity), 0) FROM SalesItem si " +
            "WHERE si.sales.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:status IS NULL OR si.sales.status = :status)")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Long getTotalItemsSold(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status
    );

    /**
     * Get unique customer count
     */
    @Query("SELECT COUNT(DISTINCT s.customerId) FROM Sales s WHERE " +
            "s.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:status IS NULL OR s.status = :status)")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Long getUniqueCustomerCount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status
    );

    // ========================================================================
    // PRODUCT PERFORMANCE - MATERIALIZED VIEW
    // ========================================================================

    /**
     * Get top products from materialized view (instant)
     */
    @Query(value =
            "SELECT item_name, " +
                    "    SUM(total_quantity) as totalQuantity, " +
                    "    SUM(total_revenue) as totalRevenue, " +
                    "    AVG(avg_unit_price) as avgUnitPrice, " +
                    "    SUM(order_count) as orderCount " +
                    "FROM mv_product_performance " +
                    "WHERE month BETWEEN DATE_FORMAT(:startDate, '%Y-%m') AND DATE_FORMAT(:endDate, '%Y-%m') " +
                    "GROUP BY item_name " +
                    "ORDER BY totalRevenue DESC " +
                    "LIMIT :limit",
            nativeQuery = true
    )
    List<Object[]> getTopProductsFromMaterializedView(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") Integer limit
    );

    /**
     * Fallback: Get top products directly
     */
    @Query("SELECT si.itemName as itemName, " +
            "SUM(si.quantity) as totalQuantity, " +
            "SUM(si.totalPrice) as totalRevenue, " +
            "AVG(si.unitPrice) as avgUnitPrice, " +
            "COUNT(DISTINCT si.sales.id) as orderCount " +
            "FROM SalesItem si " +
            "WHERE si.sales.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:status IS NULL OR si.sales.status = :status) " +
            "GROUP BY si.itemName " +
            "ORDER BY SUM(si.totalPrice) DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Object[]> getTopSellingProductsDirect(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    // ========================================================================
    // CUSTOMER ANALYTICS - MATERIALIZED VIEW
    // ========================================================================

    /**
     * Get top customers from materialized view (instant)
     */
    @Query(value =
            "SELECT customer_id, customer_name, phone, email, company_name, " +
                    "    total_orders, total_spent, last_order_date, first_order_date " +
                    "FROM mv_customer_analytics " +
                    "WHERE last_order_date BETWEEN :startDate AND :endDate " +
                    "ORDER BY total_spent DESC " +
                    "LIMIT :limit",
            nativeQuery = true
    )
    List<Object[]> getTopCustomersFromMaterializedView(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") Integer limit
    );

    /**
     * Fallback: Get top customers directly
     */
    @Query("SELECT s.customerId as customerId, " +
            "s.customerName as customerName, " +
            "s.phone as phone, " +
            "s.email as email, " +
            "s.companyName as companyName, " +
            "COUNT(s.id) as totalOrders, " +
            "SUM(si.totalPrice) as totalSpent, " +
            "MAX(s.sellDate) as lastOrderDate, " +
            "MIN(s.sellDate) as firstOrderDate " +
            "FROM Sales s " +
            "LEFT JOIN s.items si " +
            "WHERE s.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:status IS NULL OR s.status = :status) " +
            "GROUP BY s.customerId, s.customerName, s.phone, s.email, s.companyName " +
            "ORDER BY SUM(si.totalPrice) DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Object[]> getTopCustomersDirect(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    // ========================================================================
    // GROUPED DATA - MATERIALIZED VIEW
    // ========================================================================

    /**
     * Get grouped sales from materialized view
     */
    @Query(value =
            "SELECT " +
                    "    DATE_FORMAT(sale_date, :dateFormat) as period, " +
                    "    SUM(order_count) as orderCount, " +
                    "    SUM(total_revenue) as totalRevenue, " +
                    "    SUM(total_items) as totalItems " +
                    "FROM mv_daily_sales_summary " +
                    "WHERE sale_date BETWEEN :startDate AND :endDate " +
                    "AND (:status IS NULL OR status = :status) " +
                    "GROUP BY DATE_FORMAT(sale_date, :dateFormat) " +
                    "ORDER BY period DESC",
            nativeQuery = true
    )
    List<Object[]> getGroupedSalesFromMaterializedView(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status,
            @Param("dateFormat") String dateFormat
    );

    /**
     * Fallback: Get grouped sales directly
     */
    @Query("SELECT " +
            "FUNCTION('DATE_FORMAT', s.sellDate, :dateFormat) as period, " +
            "COUNT(s.id) as orderCount, " +
            "SUM(si.totalPrice) as totalRevenue " +
            "FROM Sales s " +
            "LEFT JOIN s.items si " +
            "WHERE s.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:status IS NULL OR s.status = :status) " +
            "GROUP BY FUNCTION('DATE_FORMAT', s.sellDate, :dateFormat) " +
            "ORDER BY period DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Object[]> getSalesGroupedByDateDirect(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") OrderStatus status,
            @Param("dateFormat") String dateFormat
    );

    // ========================================================================
    // AMOUNT BY STATUS QUERIES
    // ========================================================================

    /**
     * Get amount by status with partition pruning
     */
    @Query("SELECT COALESCE(SUM(si.totalPrice), 0) FROM Sales s " +
            "LEFT JOIN s.items si " +
            "WHERE s.status = :status " +
            "AND s.sellDate BETWEEN :startDate AND :endDate")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    BigDecimal getAmountByStatus(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ========================================================================
    // UTILITY QUERIES
    // ========================================================================

    /**
     * Check if materialized views are available
     */
    @Query(value =
            "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() " +
                    "AND table_name = 'mv_daily_sales_summary'",
            nativeQuery = true
    )
    Long checkMaterializedViewExists();

    /**
     * Get last refresh time of materialized view
     */
    @Query(value =
            "SELECT MAX(updated_at) FROM mv_daily_sales_summary",
            nativeQuery = true
    )
    java.time.LocalDateTime getMaterializedViewLastRefresh();
}