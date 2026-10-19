package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AvailableVehicleResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.entity.Trip;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.enums.TripStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.tms.transportationManagementSystem.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TripServiceImpl implements TripService {


    @Autowired
    private TransporterRepository transporterRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;



    @Override
    public Map<String, Long> getTripIdByShipmentId(Long shipmentId) {

        shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        Trip trip = tripRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Trip not found for this shipment."
                ));

        return Map.of("tripId", trip.getId());
    }

    @Override
    public List<AvailableVehicleResponseDTO> getAvailableVehicles(Long shipmentId) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Transporter transporter = transporterRepository
                .findByAddressNumber(currentUser.getJdeAddressNumber())
                .orElseThrow(() -> new RuntimeException("Transporter not found"));

        List<Vehicle> vehicles =
                vehicleRepository.findByTransporterAndVehicleTypeAndStatus(

                        transporter,

                        shipment.getSelectedVehicleType(),

                        "ACTIVE"
                );

        List<AvailableVehicleResponseDTO> response = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {

            AvailableVehicleResponseDTO dto =
                    new AvailableVehicleResponseDTO();

            dto.setVehicleId(vehicle.getId());

            dto.setVehicleNumber(vehicle.getVehicleNumber());

            dto.setVehicleType(vehicle.getVehicleType());

            dto.setMaxWeightCapacity(vehicle.getMaxWeightCapacity());

            dto.setMaxVolumeCapacity(vehicle.getMaxVolumeCapacity());

            response.add(dto);
        }

        return response;
    }


    @Override
    public TripResponseDTO createTrip(TripRequestDTO requestDTO) {

        // Shipment
        Shipment shipment = shipmentRepository.findById(requestDTO.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        // Shipment status validation
        if (shipment.getStatus() != ShipmentStatus.BID_FINALIZED) {
            throw new RuntimeException("Shipment is not ready for vehicle assignment.");
        }

        // Logged in user
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Logged in transporter
        Transporter transporter = transporterRepository
                .findByAddressNumber(currentUser.getJdeAddressNumber())
                .orElseThrow(() -> new RuntimeException("Transporter not found"));

        // Vehicle
        Vehicle vehicle = vehicleRepository.findById(requestDTO.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Vehicle belongs to logged in transporter
        if (!vehicle.getTransporter().getId().equals(transporter.getId())) {
            throw new RuntimeException("Vehicle does not belong to logged in transporter.");
        }

        // Vehicle type validation
        if (!vehicle.getVehicleType()
                .equalsIgnoreCase(shipment.getSelectedVehicleType())) {

            throw new RuntimeException("Selected vehicle type does not match shipment vehicle type.");
        }

        // Create Trip
        Trip trip = new Trip();

        trip.setTripNo("TRIP-" + System.currentTimeMillis());

        trip.setShipment(shipment);

        trip.setTransporter(transporter);

        trip.setVehicle(vehicle);

        trip.setDriverName(requestDTO.getDriverName());

        trip.setDriverMobile(requestDTO.getDriverMobile());

        trip.setStatus(TripStatus.CREATED);

        trip.setCreatedDate(LocalDateTime.now());   // Agar field hai

        trip = tripRepository.save(trip);

        // Update Shipment
        shipment.setVehicle(vehicle);
        shipment.setStatus(ShipmentStatus.VEHICLE_ASSIGNED);

        shipmentRepository.save(shipment);

        // Response
        TripResponseDTO response = new TripResponseDTO();

        response.setTripId(trip.getId());

        response.setTripNo(trip.getTripNo());

        response.setShipmentId(shipment.getId());

        response.setShipmentNo(shipment.getShipmentNo());

        response.setVehicleId(vehicle.getId());

        response.setVehicleNumber(vehicle.getVehicleNumber());

        response.setDriverName(trip.getDriverName());

        response.setStatus(trip.getStatus().name());

        return response;
    }
}