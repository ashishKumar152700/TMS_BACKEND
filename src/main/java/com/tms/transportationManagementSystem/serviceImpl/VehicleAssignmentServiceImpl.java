package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleAssignmentRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleAssignmentResponseDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentBid;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.entity.VehicleAssignment;
import com.tms.transportationManagementSystem.repository.ShipmentBidRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.VehicleAssignmentRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.service.VehicleAssignmentService;
import enums.ShipmentBidStatus;
import enums.ShipmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VehicleAssignmentServiceImpl
        implements VehicleAssignmentService {

    @Autowired
    private VehicleAssignmentRepository vehicleAssignmentRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ShipmentBidRepository shipmentBidRepository;

    @Override
    public VehicleAssignmentResponseDTO assignVehicle(
            Long shipmentId,
            VehicleAssignmentRequestDTO requestDTO) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() ->
                        new RuntimeException("Shipment not found"));

        Vehicle vehicle = vehicleRepository.findById(
                        requestDTO.getVehicleId())
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        ShipmentBid acceptedBid = shipmentBidRepository
                .findByShipmentIdAndStatus(
                        shipment.getId(),
                        ShipmentBidStatus.ACCEPTED
                )
                .orElseThrow(() ->
                        new RuntimeException("Accepted bid not found"));

        // transporter validation
        if (!vehicle.getTransporter().getId()
                .equals(acceptedBid.getTransporterId())) {

            throw new RuntimeException(
                    "Vehicle does not belong to accepted transporter");
        }

        VehicleAssignment assignment = new VehicleAssignment();

        assignment.setShipment(shipment);
        assignment.setVehicle(vehicle);
        assignment.setAcceptedBid(acceptedBid);
        assignment.setAssignedAt(LocalDateTime.now());

        vehicleAssignmentRepository.save(assignment);

        shipment.setVehicle(vehicle);
        shipment.setStatus(ShipmentStatus.VEHICLE_ASSIGNED);

        shipmentRepository.save(shipment);

        VehicleAssignmentResponseDTO response =
                new VehicleAssignmentResponseDTO();

        response.setAssignmentId(assignment.getId());
        response.setShipmentId(shipment.getId());
        response.setShipmentNo(shipment.getShipmentNo());
        response.setVehicleId(vehicle.getId());
        response.setVehicleNumber(vehicle.getVehicleNumber());
        response.setShipmentStatus(shipment.getStatus().name());
        response.setAssignedAt(assignment.getAssignedAt());

        return response;
    }
}