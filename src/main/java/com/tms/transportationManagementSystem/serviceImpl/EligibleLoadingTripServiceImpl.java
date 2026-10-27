
package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.EligibleLoadingTripResponseDTO;
import com.tms.transportationManagementSystem.entity.LoadingConfirmation;
import com.tms.transportationManagementSystem.entity.Trip;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.VehicleInspection;
import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import com.tms.transportationManagementSystem.repository.LoadingConfirmationRepository;
import com.tms.transportationManagementSystem.repository.VehicleInspectionRepository;
import com.tms.transportationManagementSystem.service.EligibleLoadingTripService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EligibleLoadingTripServiceImpl
        implements EligibleLoadingTripService {

    private final VehicleInspectionRepository vehicleInspectionRepository;
    private final LoadingConfirmationRepository loadingConfirmationRepository;

    @Override
    public List<EligibleLoadingTripResponseDTO> getEligibleTrips() {

        // Existing loading confirmations ko trip ID se map karo.
        Map<Long, LoadingConfirmation> confirmationByTripId =
                loadingConfirmationRepository.findAll()
                        .stream()
                        .filter(c -> c.getTrip() != null)
                        .collect(Collectors.toMap(
                                c -> c.getTrip().getId(),
                                Function.identity(),
                                (first, second) -> first
                        ));

        // Sirf accepted guard verification wale trips return honge.
        // Filter DB query mein - pehle saari inspections (files ke saath) load hoti thi.
        return vehicleInspectionRepository
                .findByGuardVerificationStatus(GuardVerificationStatus.ACCEPTED)
                .stream()
                .map(inspection -> {

                    Trip trip = inspection.getTrip();

                    if (trip == null) {
                        return null;
                    }

                    Shipment shipment = trip.getShipment();
                    Vehicle vehicle = trip.getVehicle();

                    if (shipment == null || vehicle == null) {
                        return null;
                    }

                    // Trip ka existing loading confirmation nikalo.
                    LoadingConfirmation confirmation =
                            confirmationByTripId.get(trip.getId());

                    EligibleLoadingTripResponseDTO response =
                            new EligibleLoadingTripResponseDTO();

                    response.setTripId(trip.getId());
                    response.setTripNo(trip.getTripNo());
                    response.setTripStatus(
                            trip.getStatus() != null
                                    ? trip.getStatus().name()
                                    : null
                    );

                    response.setInspectionId(inspection.getId());
                    response.setGuardVerificationStatus(
                            inspection.getGuardVerificationStatus().name()
                    );

                    response.setShipmentId(shipment.getId());
                    response.setShipmentNo(shipment.getShipmentNo());
                    response.setShipmentStatus(
                            shipment.getStatus() != null
                                    ? shipment.getStatus().name()
                                    : null
                    );
                    response.setShipmentWeight(shipment.getTotalWeight());
                    response.setShipmentVolume(shipment.getTotalVolume());

                    response.setVehicleId(vehicle.getId());
                    response.setVehicleNumber(vehicle.getVehicleNumber());
                    response.setVehicleType(vehicle.getVehicleType());
                    response.setVehicleCapacity(
                            inspection.getVehicleCapacity()
                    );
                    response.setWeightUnit(vehicle.getWeightUnit());

                    response.setDriverName(trip.getDriverName());
                    response.setDriverMobile(trip.getDriverMobile());

                    // Loading confirmation ki ID aur status response mein do.
                    // Confirmation nahi hai toh dono null rahenge.
                    if (confirmation != null) {
                        response.setLoadingConfirmationId(
                                confirmation.getId()
                        );

                        response.setLoadingConfirmationStatus(
                                confirmation.getStatus() != null
                                        ? confirmation.getStatus().name()
                                        : null
                        );
                    } else {
                        response.setLoadingConfirmationId(null);
                        response.setLoadingConfirmationStatus(null);
                    }

                    return response;
                })
                .filter(response -> response != null)
                .toList();
    }
}
