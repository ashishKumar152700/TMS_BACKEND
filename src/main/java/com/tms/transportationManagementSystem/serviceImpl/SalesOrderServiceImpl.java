package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.repository.SalesOrderRepository;
import com.tms.transportationManagementSystem.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;

    @Override
    public void syncSalesOrdersFromJde() {

        log.info("Starting Sales Order Sync From JDE");

        // JDE API call

        // Parse Response

        // Save Header

        // Save Lines

        log.info("Sales Order Sync Completed");
    }
}