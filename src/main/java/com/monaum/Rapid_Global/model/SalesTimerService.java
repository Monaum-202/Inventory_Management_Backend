package com.monaum.Rapid_Global.model;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.incomes.sales.SalesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SalesTimerService {

    private final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(10);

    @Autowired
    private SalesRepo salesRepository;

    public void startProcessingTimer(Long salesId) {

        executor.schedule(() -> {
            Sales sale = salesRepository.findById(salesId).orElse(null);

            if (sale != null && sale.getStatus() == OrderStatus.PENDING) {
                sale.setStatus(OrderStatus.PROCESSING);
                salesRepository.save(sale);
            }

        }, 1, TimeUnit.MINUTES);

    }
}
