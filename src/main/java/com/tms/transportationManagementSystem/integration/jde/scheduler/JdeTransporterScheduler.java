package com.tms.transportationManagementSystem.integration.jde.scheduler;

import com.tms.transportationManagementSystem.integration.jde.service.JdeTransporterSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JdeTransporterScheduler {

    private final JdeTransporterSyncService syncService;

//    @Scheduled(cron = "*/10 * * * * *")
//@Scheduled(cron = "0 */30 * * * *")
//@Scheduled(fixedDelay = 10000)
@Scheduled(fixedDelay = 86400000)
public void syncTransporters() {


        log.info("Starting Transporter Sync Scheduler");

        syncService.syncTransporters();
    }
}