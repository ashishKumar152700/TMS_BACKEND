package com.tms.transportationManagementSystem.integration.jde.scheduler;

import com.tms.transportationManagementSystem.integration.jde.service.JdeCustomerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JdeCustomerScheduler {

    private final JdeCustomerSyncService jdeCustomerSyncService;

    @Scheduled(fixedDelay = 10000)
    public void syncCustomers() {

        log.info("Customer Sync Started");

        jdeCustomerSyncService.syncCustomers();

        log.info("Customer Sync Finished");
    }
}