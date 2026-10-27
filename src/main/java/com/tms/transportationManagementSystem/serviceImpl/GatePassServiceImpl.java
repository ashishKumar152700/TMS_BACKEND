
package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.GatePassResponseDTO;
import com.tms.transportationManagementSystem.entity.GatePass;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.Trip;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.repository.GatePassRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.TripRepository;
import com.tms.transportationManagementSystem.service.GatePassService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatePassServiceImpl implements GatePassService {

    private final GatePassRepository gatePassRepository;
    private final TripRepository tripRepository;
    private final ShipmentRepository shipmentRepository;
    private final JdeSalesOrderRepository jdeSalesOrderRepository;

    @Override
    @Transactional
    public GatePassResponseDTO gateOut(Long gatePassId) {

        GatePass gatePass = gatePassRepository.findById(gatePassId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Gate pass not found: " + gatePassId
                        )
                );

        if (!"READY_FOR_GATE_OUT".equals(gatePass.getStatus())) {
            throw new RuntimeException(
                    "Gate out is allowed only when status is READY_FOR_GATE_OUT"
            );
        }

        Trip trip = tripRepository.findById(gatePass.getTripId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Trip not found: " + gatePass.getTripId()
                        )
                );

        // Update gate pass
        gatePass.setStatus("GATE_OUT");
        gatePass.setGateOutAt(LocalDateTime.now());

        // Update trip status
        trip.setStatus(
                com.tms.transportationManagementSystem.enums.TripStatus.IN_TRANSIT
        );

        tripRepository.save(trip);
        GatePass savedGatePass = gatePassRepository.save(gatePass);

        return mapToResponse(
                savedGatePass,
                loadShipments(List.of(savedGatePass)),
                Map.of(trip.getId(), trip)
        );
    }

    @Override
    public List<GatePassResponseDTO> getAllGatePasses() {

        List<GatePass> gatePasses = gatePassRepository.findAll();

        // Shipments aur trips ek saath load (pehle har gate pass ke liye 2 findById)
        Map<Long, Shipment> shipmentsById = loadShipments(gatePasses);

        Map<Long, Trip> tripsById =
                tripRepository.findAllById(
                                gatePasses.stream()
                                        .map(GatePass::getTripId)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet()))
                        .stream()
                        .collect(Collectors.toMap(Trip::getId, Function.identity()));

        return gatePasses.stream()
                .map(gatePass -> mapToResponse(gatePass, shipmentsById, tripsById))
                .toList();
    }

    private Map<Long, Shipment> loadShipments(List<GatePass> gatePasses) {

        return shipmentRepository.findAllById(
                        gatePasses.stream()
                                .map(GatePass::getShipmentId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(Shipment::getId, Function.identity()));
    }

    private GatePassResponseDTO mapToResponse(
            GatePass gatePass,
            Map<Long, Shipment> shipmentsById,
            Map<Long, Trip> tripsById
    ) {

        GatePassResponseDTO response = new GatePassResponseDTO();

        // Gate pass details
        response.setId(gatePass.getId());
        response.setGateId(gatePass.getGateId());
        response.setGateInAt(gatePass.getGateInAt());
        response.setGateOutAt(gatePass.getGateOutAt());
        response.setGeneratedAt(gatePass.getGeneratedAt());
        response.setStatus(gatePass.getStatus());
        response.setVehicleInspectionId(
                gatePass.getVehicleInspectionId()
        );

        // Shipment details
        Shipment shipment = shipmentsById.get(gatePass.getShipmentId());

        if (shipment != null) {

            GatePassResponseDTO.ShipmentDetails shipmentDTO =
                    new GatePassResponseDTO.ShipmentDetails();

            shipmentDTO.setId(shipment.getId());
            shipmentDTO.setShipmentNo(shipment.getShipmentNo());

            shipmentDTO.setStatus(
                    shipment.getStatus() != null
                            ? shipment.getStatus().name()
                            : null
            );

            shipmentDTO.setRouteId(shipment.getRouteId());
            shipmentDTO.setPlannedDate(shipment.getPlannedDate());
            shipmentDTO.setTotalWeight(shipment.getTotalWeight());
            shipmentDTO.setTotalVolume(shipment.getTotalVolume());
            shipmentDTO.setSelectedVehicleType(
                    shipment.getSelectedVehicleType()
            );
            shipmentDTO.setSelectedVehicleCount(
                    shipment.getSelectedVehicleCount()
            );

            // Destination city from shipment_orders -> jde_sales_orders
            String destinationCity =
                    jdeSalesOrderRepository
                            .findSalesOrdersByShipmentId(shipment.getId())
                            .stream()
                            .filter(order ->
                                    order.getShipToCity() != null
                                            && !order.getShipToCity().isBlank()
                            )
                            .map(order -> order.getShipToCity().trim())
                            .findFirst()
                            .orElse(null);

            shipmentDTO.setDestinationCity(destinationCity);

            response.setShipment(shipmentDTO);
        }

        // Trip details
        Trip trip = tripsById.get(gatePass.getTripId());

        if (trip != null) {

            GatePassResponseDTO.TripDetails tripDTO =
                    new GatePassResponseDTO.TripDetails();

            tripDTO.setId(trip.getId());
            tripDTO.setTripNo(trip.getTripNo());

            tripDTO.setStatus(
                    trip.getStatus() != null
                            ? trip.getStatus().name()
                            : null
            );

            tripDTO.setDriverName(trip.getDriverName());
            tripDTO.setDriverMobile(trip.getDriverMobile());
            tripDTO.setDispatchDate(trip.getDispatchDate());
            tripDTO.setStartTime(trip.getStartTime());
            tripDTO.setEndTime(trip.getEndTime());

            response.setTrip(tripDTO);

            // Vehicle details
            Vehicle vehicle = trip.getVehicle();

            if (vehicle != null) {

                GatePassResponseDTO.VehicleDetails vehicleDTO =
                        new GatePassResponseDTO.VehicleDetails();

                vehicleDTO.setId(vehicle.getId());
                vehicleDTO.setVehicleNumber(
                        vehicle.getVehicleNumber()
                );
                vehicleDTO.setVehicleType(
                        vehicle.getVehicleType()
                );

                response.setVehicle(vehicleDTO);
            }
        }

        return response;
    }
}
