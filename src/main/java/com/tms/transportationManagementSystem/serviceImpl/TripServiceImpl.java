package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.Trip;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.TripRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.service.TripService;
import enums.ShipmentStatus;
import enums.TripStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public TripResponseDTO createTrip(
            TripRequestDTO requestDTO) {

        Shipment shipment = shipmentRepository.findById(
                        requestDTO.getShipmentId())
                .orElseThrow(() ->
                        new RuntimeException("Shipment not found"));

        Vehicle vehicle = vehicleRepository.findById(
                        requestDTO.getVehicleId())
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        Trip trip = new Trip();

        trip.setTripNo(
                "TRIP-" + System.currentTimeMillis());

        trip.setShipment(shipment);

        trip.setVehicle(vehicle);

        trip.setDriverName(
                requestDTO.getDriverName());

        trip.setStartTime(LocalDateTime.now());

        trip.setStatus(TripStatus.CREATED);

        tripRepository.save(trip);

        shipment.setStatus(
                ShipmentStatus.IN_TRANSIT);

        shipmentRepository.save(shipment);

        TripResponseDTO response =
                new TripResponseDTO();

        response.setTripId(trip.getId());

        response.setTripNo(trip.getTripNo());

        response.setShipmentId(shipment.getId());

        response.setShipmentNo(
                shipment.getShipmentNo());

        response.setVehicleId(vehicle.getId());

        response.setVehicleNumber(
                vehicle.getVehicleNumber());

        response.setDriverName(
                trip.getDriverName());

        response.setStatus(
                trip.getStatus().name());

        response.setStartTime(
                trip.getStartTime());

        return response;
    }
}