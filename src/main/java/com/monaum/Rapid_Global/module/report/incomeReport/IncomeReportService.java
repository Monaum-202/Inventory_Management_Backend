package com.monaum.Rapid_Global.module.report.incomeReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.income.IncomeMapper;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeReportService {

    private final IncomeReportRepo incomeReportRepo;
    private final IncomeMapper incomeMapper;

    /**
     * Generate income incomeReport with filters and pagination
     */
    public ResponseEntity<BaseApiResponseDTO<?>> generateReport(
            IncomeReportFilterDto filters,
            Pageable pageable
    ) {
        Status status = filters.getStatus() != null 
            ? Status.valueOf(filters.getStatus().toUpperCase()) 
            : null;

        Page<Income> incomePage = incomeReportRepo.findIncomeReport(
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getCategoryId(),
                filters.getPaymentMethodId(),
                pageable
        );

        Page<IncomeReportResDto> reportPage = incomePage.map(this::mapToReportDto);

        CustomPageResponseDTO<IncomeReportResDto> paginatedResponse = 
                PaginationUtil.buildPageResponse(reportPage, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    /**
     * Get income incomeReport summary/analytics
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getReportSummary(
            LocalDate startDate,
            LocalDate endDate
    ) {
        IncomeReportSummaryDto summary = new IncomeReportSummaryDto();

        // Set date range
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);

        // Get totals by status
        Double approvedTotal = incomeReportRepo.getTotalByStatus(Status.APPROVED, startDate, endDate);
        Double pendingTotal = incomeReportRepo.getTotalByStatus(Status.PENDING, startDate, endDate);
        Double cancelledTotal = incomeReportRepo.getTotalByStatus(Status.CANCELED, startDate, endDate);

        summary.setApprovedIncome(approvedTotal);
        summary.setPendingIncome(pendingTotal);
        summary.setCancelledIncome(cancelledTotal);
        summary.setTotalIncome(approvedTotal + pendingTotal + cancelledTotal);

        // Get counts by status
        Long approvedCount = incomeReportRepo.getCountByStatus(Status.APPROVED, startDate, endDate);
        Long pendingCount = incomeReportRepo.getCountByStatus(Status.PENDING, startDate, endDate);
        Long cancelledCount = incomeReportRepo.getCountByStatus(Status.CANCELED, startDate, endDate);

        summary.setApprovedTransactions(approvedCount);
        summary.setPendingTransactions(pendingCount);
        summary.setCancelledTransactions(cancelledCount);
        summary.setTotalTransactions(approvedCount + pendingCount + cancelledCount);

        // Calculate average
        if (summary.getTotalTransactions() > 0) {
            summary.setAverageTransactionAmount(
                summary.getTotalIncome() / summary.getTotalTransactions()
            );
        } else {
            summary.setAverageTransactionAmount(0.0);
        }

        // Get category breakdown
        List<Object[]> categoryData = incomeReportRepo.getCategoryBreakdown(startDate, endDate);
        List<IncomeReportSummaryDto.CategoryBreakdown> categoryBreakdowns = new ArrayList<>();
        
        for (Object[] row : categoryData) {
            String categoryName = (String) row[0];
            Double totalAmount = (Double) row[1];
            Long count = (Long) row[2];
            Double percentage = approvedTotal > 0 ? (totalAmount / approvedTotal) * 100 : 0.0;
            
            categoryBreakdowns.add(new IncomeReportSummaryDto.CategoryBreakdown(
                categoryName, totalAmount, count, percentage
            ));
        }
        summary.setCategoryBreakdowns(categoryBreakdowns);

        // Get payment method breakdown
        List<Object[]> paymentData = incomeReportRepo.getPaymentMethodBreakdown(startDate, endDate);
        List<IncomeReportSummaryDto.PaymentMethodBreakdown> paymentBreakdowns = new ArrayList<>();
        
        for (Object[] row : paymentData) {
            String methodName = (String) row[0];
            Double totalAmount = (Double) row[1];
            Long count = (Long) row[2];
            Double percentage = approvedTotal > 0 ? (totalAmount / approvedTotal) * 100 : 0.0;
            
            paymentBreakdowns.add(new IncomeReportSummaryDto.PaymentMethodBreakdown(
                methodName, totalAmount, count, percentage
            ));
        }
        summary.setPaymentMethodBreakdowns(paymentBreakdowns);

        return ResponseUtils.SuccessResponseWithData(summary);
    }

    /**
     * Get daily income trend
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getDailyTrend(
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Object[]> trendData = incomeReportRepo.getDailyIncomeTrend(startDate, endDate);
        
        List<IncomeTrendDto> trends = trendData.stream()
                .map(row -> new IncomeTrendDto(
                    (LocalDate) row[0],
                    (Double) row[1],
                    (Long) row[2]
                ))
                .collect(Collectors.toList());

        return ResponseUtils.SuccessResponseWithData(trends);
    }

    /**
     * Export income incomeReport (returns all data without pagination)
     */
    public List<IncomeReportResDto> exportReport(IncomeReportFilterDto filters) {
        Status status = filters.getStatus() != null 
            ? Status.valueOf(filters.getStatus().toUpperCase()) 
            : null;

        Page<Income> incomePage = incomeReportRepo.findIncomeReport(
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getCategoryId(),
                filters.getPaymentMethodId(),
                Pageable.unpaged()
        );

        return incomePage.getContent().stream()
                .map(this::mapToReportDto)
                .collect(Collectors.toList());
    }

    /**
     * Map Income entity to IncomeReportResDto
     */
    private IncomeReportResDto mapToReportDto(Income income) {
        IncomeReportResDto dto = new IncomeReportResDto();
        
        dto.setIncomeId(income.getIncomeId());
        dto.setCategoryName(income.getIncomeCategory() != null 
            ? income.getIncomeCategory().getName() 
            : null);
        dto.setAmount(income.getAmount());
        dto.setPaymentMethodName(income.getPaymentMethod() != null 
            ? income.getPaymentMethod().getName() 
            : null);
        dto.setPaidFrom(income.getPaidFrom());
        dto.setPaidFromCompany(income.getPaidFromCompany());
        dto.setIncomeDate(income.getIncomeDate());
        dto.setDescription(income.getDescription());
        dto.setStatus(income.getStatus() != null 
            ? income.getStatus().name() 
            : null);
        dto.setApprovedByName(income.getApprovedBy() != null 
            ? income.getApprovedBy().getFullName() 
            : null);
        dto.setCreatedByName(income.getCreatedBy() != null 
            ? income.getCreatedBy().getFullName() 
            : null);
        dto.setSalesInvoiceNo(income.getSales() != null 
            ? income.getSales().getInvoiceNo() 
            : null);
        dto.setCancelReason(income.getCancelReason());
        
        return dto;
    }
}