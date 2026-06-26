package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;

import java.util.List;

public interface ShipmentBidService {

    ShipmentBidResponseDTO placeBid(
            ShipmentBidRequestDTO request,
            Long transporterId);

    List<ShipmentBidResponseDTO> getShipmentBids(Long shipmentId);

    List<ShipmentBidResponseDTO> getMyBids(Long transporterId);

    ShipmentBidResponseDTO acceptBid(Long bidId);

}