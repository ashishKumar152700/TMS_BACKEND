package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddShipmentStopDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentStopDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentStop;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.ShipmentStopRepository;
import com.tms.transportationManagementSystem.service.ShipmentStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentStopServiceImpl implements ShipmentStopService {

    private final ShipmentStopRepository shipmentStopRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    public ShipmentStopDTO createShipmentStop(AddShipmentStopDTO dto) {

        Shipment shipment = shipmentRepository.findById(dto.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        ShipmentStop shipmentStop = new ShipmentStop();

        shipmentStop.setShipment(shipment);
        shipmentStop.setStopName(dto.getStopName());
        shipmentStop.setSequenceNo(dto.getSequenceNo());

        ShipmentStop savedStop = shipmentStopRepository.save(shipmentStop);

        return mapToDTO(savedStop);
    }

    @Override
    public List<ShipmentStopDTO> getStopsByShipment(Long shipmentId) {

        List<ShipmentStop> shipmentStops =
                shipmentStopRepository.findByShipmentIdOrderBySequenceNo(shipmentId);

        return shipmentStops.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ShipmentStopDTO getShipmentStopById(Long id) {

        ShipmentStop shipmentStop = shipmentStopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment Stop not found"));

        return mapToDTO(shipmentStop);
    }

    @Override
    public ShipmentStopDTO updateShipmentStop(Long id, AddShipmentStopDTO dto) {

        ShipmentStop shipmentStop = shipmentStopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment Stop not found"));

        Shipment shipment = shipmentRepository.findById(dto.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipmentStop.setShipment(shipment);
        shipmentStop.setStopName(dto.getStopName());
        shipmentStop.setSequenceNo(dto.getSequenceNo());

        ShipmentStop updatedStop = shipmentStopRepository.save(shipmentStop);

        return mapToDTO(updatedStop);
    }

    @Override
    public void deleteShipmentStop(Long id) {

        ShipmentStop shipmentStop = shipmentStopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment Stop not found"));

        shipmentStopRepository.delete(shipmentStop);
    }

    private ShipmentStopDTO mapToDTO(ShipmentStop shipmentStop) {

        ShipmentStopDTO dto = new ShipmentStopDTO();

        dto.setId(shipmentStop.getId());

        dto.setShipmentId(shipmentStop.getShipment().getId());

        dto.setShipmentNo(shipmentStop.getShipment().getShipmentNo());

        dto.setStopName(shipmentStop.getStopName());

        dto.setSequenceNo(shipmentStop.getSequenceNo());

        dto.setArrivalTime(shipmentStop.getArrivalTime());

        dto.setDepartureTime(shipmentStop.getDepartureTime());

        return dto;
    }
}