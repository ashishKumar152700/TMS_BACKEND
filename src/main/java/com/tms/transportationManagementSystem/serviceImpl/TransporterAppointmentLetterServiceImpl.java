package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterListResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDetailsDTO;
import com.tms.transportationManagementSystem.entity.TransporterAppointmentLetter;
import java.util.List;
import com.tms.transportationManagementSystem.dto.RequestDTO.AppointmentLetterCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterResponseRequestDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.TransporterAppointmentLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterListResponseDTO;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransporterAppointmentLetterServiceImpl
        implements TransporterAppointmentLetterService {

    private final TransporterAppointmentLetterRepository letterRepository;
    private final TripRepository tripRepository;
    private final VehicleInspectionRepository inspectionRepository;
    private final ShipmentBidRepository shipmentBidRepository;
    private final JdeSalesOrderRepository salesOrderRepository;
    private final RouteRepository routeRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    public TransporterAppointmentLetter createAppointmentLetter(
            AppointmentLetterCreateRequestDTO request) {

        if (request == null || request.getTripId() == null) {
            throw new IllegalArgumentException("Trip ID is required");
        }

        validatePickupInformation(request);

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trip not found"));

        if (letterRepository.existsByTrip_Id(trip.getId())) {
            throw new IllegalStateException(
                    "Appointment letter already exists for this trip");
        }

        Shipment shipment = trip.getShipment();
        Transporter transporter = trip.getTransporter();
        Vehicle vehicle = trip.getVehicle();

        if (shipment == null || transporter == null) {
            throw new IllegalStateException(
                    "Trip must have a shipment and transporter assigned");
        }

        if (vehicle == null) {
            throw new IllegalStateException(
                    "Vehicle must be assigned before creating appointment letter");
        }

        // Appointment letter can only be issued after admin approves
        // the vehicle inspection for this exact trip.
        List<VehicleInspection> approvedInspections =
                inspectionRepository.findByTrip_IdAndReviewStatus(
                        trip.getId(),
                        InspectionReviewStatus.APPROVED
                );

        if (approvedInspections.isEmpty()) {
            throw new IllegalStateException(
                    "Appointment letter cannot be issued. "
                            + "Vehicle inspection must be APPROVED by admin first."
            );
        }

        // Fetch accepted bid for this shipment and transporter.
        ShipmentBid acceptedBid = shipmentBidRepository
                .findByShipmentIdAndBidStatusOrderByBidAmountAsc(
                        shipment.getId(),
                        ShipmentBidStatus.ACCEPTED)
                .stream()
                .filter(bid -> bid.getTransporter() != null
                        && Objects.equals(
                        bid.getTransporter().getId(),
                        transporter.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Accepted bid not found for this transporter"));

        TransporterAppointmentLetter letter =
                new TransporterAppointmentLetter();

        letter.setAppointmentNumber(
                "APT-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase());

        letter.setAppointmentDate(LocalDate.now());
        letter.setTrip(trip);
        letter.setShipment(shipment);
        letter.setTransporter(transporter);
        letter.setVehicle(vehicle);
        letter.setRemarks(request.getRemarks());

        // Shipment details
        letter.setBusinessUnit(request.getBusinessUnit());
        letter.setRouteCode(null);

        letter.setSalesOrderNumbers(
                salesOrderRepository.findSalesOrdersByShipmentId(
                                shipment.getId())
                        .stream()
                        .map(JdeSalesOrder::getOrderNumber)
                        .filter(Objects::nonNull)
                        .distinct()
                        .reduce((a, b) -> a + ", " + b)
                        .orElse(null));

        List<JdeSalesOrder> orders =
                salesOrderRepository.findSalesOrdersByShipmentId(
                        shipment.getId());

        if (!orders.isEmpty()) {
            JdeSalesOrder firstOrder = orders.get(0);

            letter.setCompany(firstOrder.getCompany());
            letter.setBranchPlant(firstOrder.getBranchPlant());
            letter.setCustomer(firstOrder.getShipToCustomerName());

            String address = firstOrder.getShipToAddress1();
            String city = firstOrder.getShipToCity();
            String state = firstOrder.getShipToState();
            String pinCode = firstOrder.getShipToPinCode();

            String deliveryLocation = List.of(
                            address, city, state, pinCode)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(null);

            letter.setDeliveryLocation(deliveryLocation);

            // Cargo category fallback: first JDE line description.
            letter.setProductCategory(
                    request.getProductCategory() != null
                            ? request.getProductCategory()
                            : orders.stream()
                            .filter(o -> o.getLines() != null)
                            .flatMap(o -> o.getLines().stream())
                            .map(JdeSalesOrderLine::getDescription)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null));
        } else {
            letter.setProductCategory(request.getProductCategory());
        }

        // Route details from route master
        if (shipment.getRouteId() != null) {
            Route route = routeRepository.findById(shipment.getRouteId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Route not found for shipment"));

            letter.setRouteCode(route.getRouteCode());
            letter.setRoute(
                    route.getSourceLocation() + " to "
                            + route.getDestinationLocation());
            letter.setExpectedTransitTime(route.getEstimatedTime());
        }

        // Pickup details are entered by Admin.
        letter.setPickupLocation(request.getPickupLocation().trim());
        letter.setReportingGate(request.getReportingGate().trim());
        letter.setLoadingBay(request.getLoadingBay().trim());
        letter.setReportingDate(request.getReportingDate());
        letter.setReportingTime(request.getReportingTime());
        letter.setLoadingWindow(request.getLoadingWindow().trim());

        // Cargo quantities
        letter.setTotalWeight(shipment.getTotalWeight());
        letter.setTotalVolume(shipment.getTotalVolume());
        letter.setNumberOfPackages(request.getNumberOfPackages());
        letter.setSpecialHandling(request.getSpecialHandling());

        // Approved freight comes from accepted bid.
        letter.setApprovedFreight(
                BigDecimal.valueOf(acceptedBid.getBidAmount()));

        letter.setFreightType(request.getFreightType());

        if (request.getAdditionalCharges() != null) {
            letter.setAdditionalCharges(
                    BigDecimal.valueOf(request.getAdditionalCharges()));
        }

        // Delivery date = accepted bid pickup date + delivery days.
        if (acceptedBid.getPlannedPickupDate() != null
                && acceptedBid.getDeliveryDays() != null) {
            letter.setExpectedDeliveryDate(
                    acceptedBid.getPlannedPickupDate()
                            .plusDays(acceptedBid.getDeliveryDays()));
        }

        letter.setStatus("ISSUED");

        // Save the appointment letter first.
        TransporterAppointmentLetter savedLetter =
                letterRepository.save(letter);

        // Update this shipment only after the appointment letter is issued
        // for the trip associated with this shipment.
        shipment.setStatus(ShipmentStatus.APPOINTMENT_LETTER_ISSUED);
        shipmentRepository.save(shipment);

        return savedLetter;
    }

    private void validatePickupInformation(
            AppointmentLetterCreateRequestDTO request) {

        if (isBlank(request.getPickupLocation())
                || isBlank(request.getReportingGate())
                || isBlank(request.getLoadingBay())
                || request.getReportingDate() == null
                || request.getReportingTime() == null
                || isBlank(request.getLoadingWindow())) {
            throw new IllegalArgumentException(
                    "Pickup location, reporting gate, loading bay, "
                            + "reporting date/time and loading window are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentLetterListResponseDTO> getAllAppointmentLetters(
            String status) {

        List<TransporterAppointmentLetter> letters;

        if (status == null || status.isBlank()
                || status.equalsIgnoreCase("ALL")) {
            letters = letterRepository.findAll();
        } else {
            letters = letterRepository.findByStatusIgnoreCase(status);
        }

        return letters.stream()
                .map(this::mapToAppointmentLetterDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public TransporterAppointmentLetter getAppointmentLetterById(Long id) {
        return letterRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Appointment letter not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentLetterListResponseDTO> getTransporterAppointmentLetters(
            Long transporterId) {

        return letterRepository
                .findByTransporter_IdOrderByCreatedAtDesc(transporterId)
                .stream()
                .map(this::mapToAppointmentLetterDTO)
                .toList();
    }



    private AppointmentLetterListResponseDTO mapToAppointmentLetterDTO(
            TransporterAppointmentLetter letter) {

        AppointmentLetterListResponseDTO dto =
                new AppointmentLetterListResponseDTO();

        dto.setId(letter.getId());
        dto.setAppointmentNumber(letter.getAppointmentNumber());
        dto.setRouteName(letter.getRoute());
        dto.setReportingDate(letter.getReportingDate());
        dto.setReportingTime(letter.getReportingTime());
        dto.setStatus(letter.getStatus());
        dto.setPickupLocation(letter.getPickupLocation());
        dto.setReportingGate(letter.getReportingGate());
        dto.setLoadingBay(letter.getLoadingBay());
        dto.setLoadingWindow(letter.getLoadingWindow());

        if (letter.getTrip() != null) {
            dto.setTripId(letter.getTrip().getId());

            // Driver name trips table se
            dto.setDriverName(letter.getTrip().getDriverName());
        }

        // Vehicle details
        Vehicle vehicle = letter.getVehicle();

        if (vehicle == null && letter.getTrip() != null) {
            vehicle = letter.getTrip().getVehicle();
        }

        if (vehicle != null) {
            VehicleDetailsDTO vehicleDTO = new VehicleDetailsDTO();

            vehicleDTO.setId(vehicle.getId());
            vehicleDTO.setVehicleNumber(vehicle.getVehicleNumber());
            vehicleDTO.setVehicleType(vehicle.getVehicleType());
            vehicleDTO.setVehicleCategory(vehicle.getVehicleCategory());
            vehicleDTO.setMaxWeightCapacity(vehicle.getMaxWeightCapacity());
            vehicleDTO.setMaxVolumeCapacity(vehicle.getMaxVolumeCapacity());
            vehicleDTO.setGpsDeviceId(vehicle.getGpsDeviceId());
            vehicleDTO.setStatus(vehicle.getStatus());
            vehicleDTO.setWeightUnit(vehicle.getWeightUnit());
            vehicleDTO.setVolumeUnit(vehicle.getVolumeUnit());

            if (vehicle.getTransporter() != null) {
                vehicleDTO.setTransporterId(
                        vehicle.getTransporter().getId()
                );
                vehicleDTO.setTransporterName(null);
            }

            dto.setVehicle(vehicleDTO);
        }

        // Shipment details
        Shipment shipment = letter.getShipment();

        if (shipment == null && letter.getTrip() != null) {
            shipment = letter.getTrip().getShipment();
        }

        if (shipment != null) {
            ShipmentDetailsDTO shipmentDTO = new ShipmentDetailsDTO();

            shipmentDTO.setId(shipment.getId());
            shipmentDTO.setShipmentNo(shipment.getShipmentNo());
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
            shipmentDTO.setStatus(
                    shipment.getStatus() != null
                            ? shipment.getStatus().toString()
                            : null
            );
            shipmentDTO.setExpectedPrice(shipment.getExpectedPrice());
            shipmentDTO.setPlannedPickupDate(shipment.getPlannedPickupDate());
            shipmentDTO.setBidMessage(shipment.getBidMessage());

            dto.setShipment(shipmentDTO);
        }

        return dto;
    }



    @Override
    public TransporterAppointmentLetter respondToAppointmentLetter(
            Long id,
            Long transporterId,
            AppointmentLetterResponseRequestDTO request) {

        if (request == null || request.getAccepted() == null) {
            throw new IllegalArgumentException(
                    "Transporter acceptance (true/false) is required");
        }

        TransporterAppointmentLetter letter =
                getAppointmentLetterById(id);

        if (!Objects.equals(
                letter.getTransporter().getId(), transporterId)) {
            throw new IllegalArgumentException(
                    "This appointment letter does not belong to this transporter");
        }

        if (!"ISSUED".equalsIgnoreCase(letter.getStatus())) {
            throw new IllegalStateException(
                    "Only ISSUED appointment letters can be accepted or rejected");
        }

        if (!request.getAccepted()
                && (request.getRemarks() == null
                || request.getRemarks().isBlank())) {
            throw new IllegalArgumentException(
                    "Remarks are required when rejecting an appointment");
        }

        if (request.getAccepted()
                && (request.getExpectedArrivalDate() == null
                || request.getExpectedArrivalTime() == null)) {
            throw new IllegalArgumentException(
                    "Expected arrival date and time are required for acceptance");
        }

        letter.setTransporterAcceptance(request.getAccepted());
        letter.setTransporterRemarks(request.getRemarks());
        letter.setExpectedArrivalDate(request.getExpectedArrivalDate());
        letter.setExpectedArrivalTime(request.getExpectedArrivalTime());
        letter.setTransporterSignatureUrl(request.getSignatureUrl());

        letter.setStatus(
                request.getAccepted() ? "ACCEPTED" : "REJECTED");

        return letterRepository.save(letter);
    }

    @Override
    public TransporterAppointmentLetter updateAppointmentLetterStatus(
            Long id,
            String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        String normalized = status.trim().toUpperCase();

        if (!List.of("ISSUED", "CANCELLED").contains(normalized)) {
            throw new IllegalArgumentException(
                    "Admin can set status to ISSUED or CANCELLED");
        }

        TransporterAppointmentLetter letter = getAppointmentLetterById(id);

        if ("ACCEPTED".equalsIgnoreCase(letter.getStatus())
                || "REJECTED".equalsIgnoreCase(letter.getStatus())) {
            throw new IllegalStateException(
                    "Cannot change status after transporter response");
        }

        letter.setStatus(normalized);

        return letterRepository.save(letter);
    }
}
