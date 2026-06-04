package com.tms.transportationManagementSystem.integration.jde.scheduler;

import com.tms.transportationManagementSystem.integration.jde.service.JdeSalesOrderSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JdeSalesOrderScheduler {

    private final JdeSalesOrderSyncService syncService;

    @Scheduled(cron = "0 */15 * * * *")
    public void syncSalesOrders() {

        log.info("Starting Sales Order Sync");

        syncService.syncSalesOrders();

        log.info("Sales Order Sync Completed");
    }
}