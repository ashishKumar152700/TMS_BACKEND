package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrdersToShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.SelectVehicleSuggestionDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleSuggestionDTO;

import java.util.List;

public interface ShipmentService {

    ShipmentDTO createShipment(
            CreateShipmentDTO createShipmentDTO
    );

    List<VehicleSuggestionDTO>
    getVehicleSuggestions(Long shipmentId);

    ShipmentDTO selectVehicleSuggestion(

            Long shipmentId,

            SelectVehicleSuggestionDTO dto
    );

    ShipmentDetailsDTO getShipmentById(Long shipmentId);

    List<ShipmentDetailsDTO> getAllShipmentsWithOrders();

    ShipmentDTO removeOrderFromShipment(
            Long shipmentId,
            Long orderId
    );

    ShipmentDTO addOrdersToShipment(
            Long shipmentId,
            AddOrdersToShipmentDTO dto
    );
}