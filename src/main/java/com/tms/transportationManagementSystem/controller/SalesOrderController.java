package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncSalesOrders() {

        salesOrderService.syncSalesOrdersFromJde();

        return ResponseEntity.ok("Sales Orders Synced Successfully");
    }
}