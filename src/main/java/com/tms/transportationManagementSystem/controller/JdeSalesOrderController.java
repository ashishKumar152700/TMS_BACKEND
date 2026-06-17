package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AssignRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderRouteGroupingResponseDTO;
import com.tms.transportationManagementSystem.service.JdeSalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/group-by-route")
    public OrderRouteGroupingResponseDTO groupOrdersByRoute() {
        return salesOrderService.groupOrdersByRoute();
    }

    @PutMapping("/orders/assign-route")
    public ResponseEntity<?> assignRoute(
            @RequestBody AssignRouteRequestDTO request) {

        return ResponseEntity.ok(
                salesOrderService.assignRoute(request)
        );
    }

    @GetMapping("/orders/{orderId}/available-routes")
    public ResponseEntity<?> getRoutes(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                salesOrderService.getAvailableRoutes(orderId)
        );
    }
}