package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppliedBidResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidManagementResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipment-bids")
@RequiredArgsConstructor
public class ShipmentBidController {

    private final ShipmentBidService shipmentBidService;

    // Transporter Place Bid
    @PostMapping
    public ShipmentBidResponseDTO placeBid(
            @RequestBody ShipmentBidRequestDTO request,
            @RequestParam Long transporterId) {

        return shipmentBidService.placeBid(request, transporterId);
    }

    // Transporter My Bids
    @GetMapping("/my/{transporterId}")
    public List<ShipmentBidResponseDTO> getMyBids(
            @PathVariable Long transporterId) {

        return shipmentBidService.getMyBids(transporterId);
    }

    // Admin - All bids of shipment
    @GetMapping("/shipment/{shipmentId}")
    public List<ShipmentBidResponseDTO> getShipmentBids(
            @PathVariable Long shipmentId) {

        return shipmentBidService.getShipmentBids(shipmentId);
    }

    // Admin Accept Bid
    @PutMapping("/{bidId}/accept")
    public ShipmentBidResponseDTO acceptBid(
            @PathVariable Long bidId) {

        return shipmentBidService.acceptBid(bidId);
    }
    @GetMapping("/my-bids")
    public ResponseEntity<List<AppliedBidResponseDTO>> getMyAppliedBids() {

        return ResponseEntity.ok(
                shipmentBidService.getMyAppliedBids());
    }

    @GetMapping("/admin/all")
    public List<ShipmentBidManagementResponseDTO> getAllShipmentBidsForAdmin() {

        return shipmentBidService.getAllShipmentBidsForAdmin();
    }

    @PutMapping("/{bidId}/reject")
    public ShipmentBidResponseDTO rejectBid(
            @PathVariable Long bidId) {

        return shipmentBidService.rejectBid(bidId);
    }

}