package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentBidDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidDTO;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipment-bids")
public class ShipmentBidController {

    private final ShipmentBidService
            shipmentBidService;

    public ShipmentBidController(
            ShipmentBidService shipmentBidService
    ) {
        this.shipmentBidService =
                shipmentBidService;
    }

    @PostMapping
    public ShipmentBidDTO createBid(

            @RequestBody
            CreateShipmentBidDTO dto
    ) {

        return shipmentBidService
                .createBid(dto);
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<ShipmentBidDTO>
    getBidsByShipment(

            @PathVariable Long shipmentId
    ) {

        return shipmentBidService
                .getBidsByShipment(
                        shipmentId
                );
    }

    @PostMapping("/{bidId}/accept")
    public ShipmentBidDTO acceptBid(

            @PathVariable Long bidId
    ) {

        return shipmentBidService
                .acceptBid(bidId);
    }
}