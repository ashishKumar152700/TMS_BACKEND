package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;

import java.util.List;

public interface ShipmentBidService {

    String openBidding(Long shipmentId);

    ShipmentBidResponseDTO placeBid(
            ShipmentBidRequestDTO request);

    List<ShipmentBidResponseDTO>
    getShipmentBids(Long shipmentId);

    String acceptBid(Long bidId);
}