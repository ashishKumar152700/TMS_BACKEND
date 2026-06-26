package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.OpenBidRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;

import java.util.List;

public interface ShipmentService {

    ShipmentResponseDTO createShipment(
            ShipmentCreateRequestDTO request);

    List<ShipmentResponseDTO> getAllShipments();

//    ShipmentResponseDTO openBid(Long shipmentId);

    ShipmentResponseDTO openBid(
            Long shipmentId,
            OpenBidRequestDTO request);

    List<Shipment> findByStatus(
            ShipmentStatus status);

//    List<ShipmentResponseDTO> getOpenBids(Long transporterId);
List<ShipmentResponseDTO> getOpenBids(String token);

}