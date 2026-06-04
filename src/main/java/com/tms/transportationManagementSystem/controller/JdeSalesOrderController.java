package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.integration.jde.service.JdeSalesOrderSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jde/sales-orders")
@RequiredArgsConstructor
public class JdeSalesOrderController {

    private final JdeSalesOrderSyncService syncService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncSalesOrders() {

        syncService.syncSalesOrders();

        return ResponseEntity.ok(
                "Sales Orders Synced Successfully");
    }
}