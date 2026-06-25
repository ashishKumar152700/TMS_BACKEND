package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ShipmentResponseDTO createShipment(
            @RequestBody ShipmentCreateRequestDTO request) {

        System.out.println("========== SHIPMENT REQUEST ==========");
        System.out.println(request);
        System.out.println("OrderIds              : " + request.getOrderIds());
        System.out.println("RouteId               : " + request.getRouteId());
        System.out.println("VehicleId             : " + request.getVehicleId());
        System.out.println("SelectedVehicleType   : " + request.getSelectedVehicleType());
        System.out.println("SelectedVehicleCount  : " + request.getSelectedVehicleCount());
        System.out.println("======================================");

        return shipmentService.createShipment(request);
    }
}