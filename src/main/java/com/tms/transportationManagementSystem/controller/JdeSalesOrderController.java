package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.service.JdeSalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jde/sales-orders")
public class JdeSalesOrderController {

    private final JdeSalesOrderService salesOrderService;

    @GetMapping("/getAll")
    public List<JdeSalesOrderResponseDTO> getAllSalesOrders() {

        return salesOrderService.getAllSalesOrders();
    }
}