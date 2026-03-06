package com.monaum.Rapid_Global.module.report.salesReport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Materialized View Refresh Service
 * <p>
 * Schedules:
 * - Daily summary: Refreshed every hour
 * - Monthly summary: Refreshed daily at 2 AM
 * - Product performance: Refreshed daily at 3 AM
 * - Customer analytics: Refreshed daily at 4 AM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterializedViewRefreshService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Refresh daily sales summary - runs every hour
     */
    @Scheduled(cron = "0 0 * * * ?") // Every hour at minute 0
    public void refreshDailySalesSummary() {
        try {
            log.info("Starting daily sales summary refresh...");
            long startTime = System.currentTimeMillis();

            jdbcTemplate.execute("CALL refresh_daily_sales_summary()");

            long duration = System.currentTimeMillis() - startTime;
            log.info("Daily sales summary refreshed successfully in {}ms", duration);

        } catch (Exception e) {
            log.error("Error refreshing daily sales summary", e);
        }
    }

    /**
     * Refresh monthly sales summary - runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshMonthlySalesSummary() {
        try {
            log.info("Starting monthly sales summary refresh...");
            long startTime = System.currentTimeMillis();

            jdbcTemplate.execute("CALL refresh_monthly_sales_summary()");

            long duration = System.currentTimeMillis() - startTime;
            log.info("Monthly sales summary refreshed successfully in {}ms", duration);

        } catch (Exception e) {
            log.error("Error refreshing monthly sales summary", e);
        }
    }

    /**
     * Refresh product performance - runs daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void refreshProductPerformance() {
        try {
            log.info("Starting product performance refresh...");
            long startTime = System.currentTimeMillis();

            // Delete current month data
            jdbcTemplate.update("DELETE FROM mv_product_performance WHERE month = DATE_FORMAT(CURDATE(), '%Y-%m')");

            // Refresh current month
            jdbcTemplate.update("""
                    INSERT INTO mv_product_performance
                    (item_name, month, total_quantity, total_revenue, avg_unit_price, order_count)
                    
                    SELECT
                        si.item_name,
                        DATE_FORMAT(s.sell_date, '%Y-%m') AS month,
                    
                        SUM(si.quantity),
                        SUM(si.total_price),
                        AVG(si.unit_price),
                        COUNT(DISTINCT s.id)
                    
                    FROM sales_item si
                    JOIN sales s ON si.sales_id = s.id
                    
                    WHERE DATE_FORMAT(s.sell_date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')
                    AND s.status <> 'CANCELLED'
                    
                    GROUP BY si.item_name, month
                    
                    ON DUPLICATE KEY UPDATE
                        total_quantity = VALUES(total_quantity),
                        total_revenue = VALUES(total_revenue),
                        avg_unit_price = VALUES(avg_unit_price),
                        order_count = VALUES(order_count),
                        updated_at = CURRENT_TIMESTAMP
                    """);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Product performance refreshed successfully in {}ms", duration);

        } catch (Exception e) {
            log.error("Error refreshing product performance", e);
        }
    }

    /**
     * Refresh customer analytics - runs daily at 4 AM
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void refreshCustomerAnalytics() {
        try {
            log.info("Starting customer analytics refresh...");
            long startTime = System.currentTimeMillis();

            // Full refresh
            jdbcTemplate.update("TRUNCATE TABLE mv_customer_analytics");

            jdbcTemplate.update("INSERT INTO mv_customer_analytics " + "(customer_id, customer_name, phone, email, total_orders, total_spent, last_order_date, first_order_date) " + "SELECT s.customer_id, s.customer_name, s.phone, s.email, " + "COUNT(*) as total_orders, COALESCE(SUM(si.total_price), 0) as total_spent, " + "MAX(s.sell_date) as last_order_date, MIN(s.sell_date) as first_order_date " + "FROM Sales s LEFT JOIN sales_item si ON s.id = si.sales_id " + "WHERE s.status != 'CANCELLED' " + "GROUP BY s.customer_id, s.customer_name, s.phone, s.email");

            long duration = System.currentTimeMillis() - startTime;
            log.info("Customer analytics refreshed successfully in {}ms", duration);

        } catch (Exception e) {
            log.error("Error refreshing customer analytics", e);
        }
    }

    /**
     * Refresh all materialized views - manual trigger
     */
    public void refreshAllViews() {
        log.info("=== Starting full materialized view refresh ===");
        refreshDailySalesSummary();
        refreshMonthlySalesSummary();
        refreshProductPerformance();
        refreshCustomerAnalytics();
        log.info("=== Completed full materialized view refresh ===");
    }

    /**
     * Get last refresh timestamp
     */
    public LocalDateTime getLastRefreshTime(String viewName) {
        try {
            String query = String.format("SELECT MAX(updated_at) FROM %s", viewName);
            return jdbcTemplate.queryForObject(query, LocalDateTime.class);
        } catch (Exception e) {
            log.warn("Could not get last refresh time for {}: {}", viewName, e.getMessage());
            return null;
        }
    }
}