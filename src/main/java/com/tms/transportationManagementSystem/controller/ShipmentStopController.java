package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddShipmentStopDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentStopDTO;
import com.tms.transportationManagementSystem.service.ShipmentStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipment-stops")
@RequiredArgsConstructor
public class ShipmentStopController {

    private final ShipmentStopService shipmentStopService;

    @PostMapping
    public ShipmentStopDTO createShipmentStop(
            @RequestBody AddShipmentStopDTO dto
    ) {
        return shipmentStopService.createShipmentStop(dto);
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<ShipmentStopDTO> getStopsByShipment(
            @PathVariable Long shipmentId
    ) {
        return shipmentStopService.getStopsByShipment(shipmentId);
    }

    @GetMapping("/{id}")
    public ShipmentStopDTO getShipmentStopById(
            @PathVariable Long id
    ) {
        return shipmentStopService.getShipmentStopById(id);
    }

    @PutMapping("/{id}")
    public ShipmentStopDTO updateShipmentStop(
            @PathVariable Long id,
            @RequestBody AddShipmentStopDTO dto
    ) {
        return shipmentStopService.updateShipmentStop(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteShipmentStop(
            @PathVariable Long id
    ) {

        shipmentStopService.deleteShipmentStop(id);

        return "Shipment Stop deleted successfully";
    }
}