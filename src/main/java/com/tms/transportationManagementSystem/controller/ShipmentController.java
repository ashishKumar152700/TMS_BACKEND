package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.OpenBidRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ShipmentResponseDTO createShipment(
            @RequestBody ShipmentCreateRequestDTO request) {

        return shipmentService.createShipment(request);
    }

    @GetMapping
    public List<ShipmentResponseDTO> getAllShipments() {
        return shipmentService.getAllShipments();
    }
    @PutMapping("/{shipmentId}/open-bid")
    public ResponseEntity<ShipmentResponseDTO> openBid(
            @PathVariable Long shipmentId,
            @RequestBody OpenBidRequestDTO request){

        return ResponseEntity.ok(
                shipmentService.openBid(
                        shipmentId,
                        request));
    }

    @GetMapping("/open-bids")
    public List<ShipmentResponseDTO> getOpenBids(
            @RequestHeader("Authorization") String token) {

        return shipmentService.getOpenBids(token);
    }


}