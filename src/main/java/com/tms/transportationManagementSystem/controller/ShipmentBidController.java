package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class ShipmentBidController {

    private final ShipmentBidService shipmentBidService;

    @PostMapping("/open/{shipmentId}")
    public String openBidding(
            @PathVariable Long shipmentId) {

        return shipmentBidService
                .openBidding(shipmentId);
    }

    @PostMapping("/place")
    public ShipmentBidResponseDTO placeBid(
            @RequestBody ShipmentBidRequestDTO request) {

        return shipmentBidService
                .placeBid(request);
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<ShipmentBidResponseDTO>
    getShipmentBids(
            @PathVariable Long shipmentId) {

        return shipmentBidService
                .getShipmentBids(shipmentId);
    }

    @PostMapping("/accept/{bidId}")
    public String acceptBid(
            @PathVariable Long bidId) {

        return shipmentBidService
                .acceptBid(bidId);
    }
}