package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrdersToShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.SelectVehicleSuggestionDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleSuggestionDTO;
import com.tms.transportationManagementSystem.service.ShipmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/{shipmentId}/orders")
    public ShipmentDTO addOrdersToShipment(

            @PathVariable Long shipmentId,

            @RequestBody AddOrdersToShipmentDTO dto
    ) {

        return shipmentService
                .addOrdersToShipment(
                        shipmentId,
                        dto
                );
    }


    @DeleteMapping("/{shipmentId}/orders/{orderId}")
    public ShipmentDTO removeOrderFromShipment(

            @PathVariable Long shipmentId,

            @PathVariable Long orderId
    ) {

        return shipmentService
                .removeOrderFromShipment(
                        shipmentId,
                        orderId
                );
    }


    @GetMapping("/{shipmentId}")
    public ShipmentDetailsDTO getShipmentById(
            @PathVariable Long shipmentId
    ) {
        return shipmentService.getShipmentById(shipmentId);
    }


    public ShipmentController(
            ShipmentService shipmentService
    ) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/with-orders")
    public List<ShipmentDetailsDTO>
    getAllShipmentsWithOrders() {

        return shipmentService
                .getAllShipmentsWithOrders();
    }

    @PostMapping
    public ShipmentDTO createShipment(
            @RequestBody
            CreateShipmentDTO createShipmentDTO
    ) {

        return shipmentService.createShipment(
                createShipmentDTO
        );
    }

    @GetMapping("/{shipmentId}/vehicle-suggestions")
    public List<VehicleSuggestionDTO>
    getVehicleSuggestions(

            @PathVariable Long shipmentId
    ) {

        return shipmentService
                .getVehicleSuggestions(shipmentId);
    }

    @PostMapping(
            "/{shipmentId}/select-vehicle-suggestion"
    )
    public ShipmentDTO selectVehicleSuggestion(

            @PathVariable Long shipmentId,

            @RequestBody
            SelectVehicleSuggestionDTO dto
    ) {

        return shipmentService
                .selectVehicleSuggestion(
                        shipmentId,
                        dto
                );
    }
}