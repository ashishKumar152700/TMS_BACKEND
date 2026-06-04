package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddShipmentStopDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentStopDTO;

import java.util.List;

public interface ShipmentStopService {

    ShipmentStopDTO createShipmentStop(AddShipmentStopDTO dto);

    List<ShipmentStopDTO> getStopsByShipment(Long shipmentId);

    ShipmentStopDTO getShipmentStopById(Long id);

    ShipmentStopDTO updateShipmentStop(Long id, AddShipmentStopDTO dto);

    void deleteShipmentStop(Long id);
}