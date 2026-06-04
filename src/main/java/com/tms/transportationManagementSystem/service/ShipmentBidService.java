package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentBidDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidDTO;

import java.util.List;

public interface ShipmentBidService {

    ShipmentBidDTO createBid(
            CreateShipmentBidDTO dto
    );

    List<ShipmentBidDTO> getBidsByShipment(
            Long shipmentId
    );

    ShipmentBidDTO acceptBid(Long bidId);
}