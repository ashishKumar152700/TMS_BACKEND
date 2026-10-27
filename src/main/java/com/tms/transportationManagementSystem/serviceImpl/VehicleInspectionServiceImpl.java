package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.GuardVerificationRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionAdminResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionTransporterResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionShipmentResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.SalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.SalesOrderLineResponseDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.InspectionResult;
import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.VehicleInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tms.transportationManagementSystem.repository.GatePassRepository;
import java.util.UUID;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VehicleInspectionServiceImpl
        implements VehicleInspectionService {

    private final VehicleInspectionRepository vehicleInspectionRepository;

    private final TripRepository tripRepository;

    private final ShipmentOrderRepository shipmentOrderRepository;

    private final JdeSalesOrderRepository jdeSalesOrderRepository;

    private final TransporterAppointmentLetterRepository transporterAppointmentLetterRepository;

    private final GatePassRepository gatePassRepository;

    private final ShipmentRepository shipmentRepository;

    @Override
    @Transactional
    public VehicleInspectionResponseDTO verifyVehicleAtGate(
            Long id,
            GuardVerificationRequestDTO requestDTO) {

        if (requestDTO == null || requestDTO.getStatus() == null) {
            throw new RuntimeException(
                    "Guard verification status is required");
        }

        GuardVerificationStatus status = requestDTO.getStatus();

        if (status != GuardVerificationStatus.ACCEPTED
                && status != GuardVerificationStatus.REJECTED
                && status != GuardVerificationStatus.ON_HOLD) {
            throw new RuntimeException(
                    "Allowed statuses: ACCEPTED, REJECTED, ON_HOLD");
        }

        if ((status == GuardVerificationStatus.REJECTED
                || status == GuardVerificationStatus.ON_HOLD)
                && (requestDTO.getRemarks() == null
                || requestDTO.getRemarks().isBlank())) {
            throw new RuntimeException(
                    "Remarks are required for REJECTED and ON_HOLD status");
        }

        VehicleInspection inspection = vehicleInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle inspection not found: " + id));

        GuardVerificationStatus current =
                inspection.getGuardVerificationStatus();

        if (current == GuardVerificationStatus.ACCEPTED
                || current == GuardVerificationStatus.REJECTED) {
            throw new RuntimeException(
                    "Guard verification is already finalized: " + current);
        }

        Trip trip = inspection.getTrip();

        if (trip == null) {
            throw new RuntimeException(
                    "Trip is not associated with inspection: " + id);
        }

        Shipment shipment = trip.getShipment();

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment is not associated with trip: " + trip.getId());
        }

        TransporterAppointmentLetter appointmentLetter =
                transporterAppointmentLetterRepository
                        .findByTrip_Id(trip.getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Appointment letter not found for this trip"
                        ));

        if (!"ACCEPTED".equalsIgnoreCase(appointmentLetter.getStatus())
                || !Boolean.TRUE.equals(appointmentLetter.getTransporterAcceptance())) {

            throw new RuntimeException(
                    "Transporter must accept the appointment letter before guard verification"
            );
        }

        inspection.setGuardVerificationStatus(status);
        inspection.setGuardVerificationRemarks(requestDTO.getRemarks());
        inspection.setGuardVerifiedAt(LocalDateTime.now());

        VehicleInspection savedInspection =
                vehicleInspectionRepository.save(inspection);

        // Gate ID sirf ACCEPTED verification par generate hogi.
        if (status == GuardVerificationStatus.ACCEPTED) {

            boolean alreadyExists =
                    gatePassRepository.existsByVehicleInspectionId(
                            savedInspection.getId());

            if (!alreadyExists) {
                GatePass gatePass = new GatePass();

                gatePass.setVehicleInspectionId(savedInspection.getId());
                gatePass.setTripId(trip.getId());
                gatePass.setShipmentId(shipment.getId());

                gatePass.setGateId(
                        "GATE-" + UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 12)
                                .toUpperCase()
                );

                gatePass.setStatus("GENERATED");
                // Guard ACCEPTED means vehicle entry is recorded.
                gatePass.setGateInAt(LocalDateTime.now());
                gatePass.setGateOutAt(null);

                gatePassRepository.save(gatePass);
            }
        }

        return mapToResponse(savedInspection);
    }



    @Override
    public VehicleInspectionResponseDTO createInspection(
            VehicleInspectionRequestDTO requestDTO
    ) throws IOException {

        if (requestDTO.getTripId() == null) {
            throw new RuntimeException("Trip ID is required");
        }

        if (vehicleInspectionRepository.existsByTripId(
                requestDTO.getTripId())) {

            throw new RuntimeException(
                    "Inspection already exists for this trip"
            );
        }

        Trip trip = tripRepository.findById(
                requestDTO.getTripId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Trip not found: " + requestDTO.getTripId()
                )
        );

        Vehicle vehicle = trip.getVehicle();

        if (vehicle == null) {
            throw new RuntimeException(
                    "Vehicle is not assigned to this trip"
            );
        }

        Shipment shipment = trip.getShipment();

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment is not associated with this trip"
            );
        }

        VehicleInspection inspection =
                new VehicleInspection();

        inspection.setTrip(trip);


        // 1. Transporter & Vehicle Identification

        inspection.setCarrierCode(
                requestDTO.getCarrierCode()
        );

        inspection.setArrivalDate(
                requestDTO.getArrivalDate()
        );

        inspection.setArrivalTime(
                requestDTO.getArrivalTime()
        );

        inspection.setGateEntryNo(
                requestDTO.getGateEntryNo()
        );


        // 2. Driver Inspection

        inspection.setDrivingLicenseNumber(
                requestDTO.getDrivingLicenseNumber()
        );

        inspection.setLicenseValidity(
                requestDTO.getLicenseValidity()
        );

        inspection.setDriverIdGatePass(
                requestDTO.getDriverIdGatePass()
        );


        // 3. Vehicle Documents

        inspection.setRcAvailable(
                requestDTO.getRcAvailable()
        );

        inspection.setInsuranceAvailable(
                requestDTO.getInsuranceAvailable()
        );

        inspection.setFitnessCertificateAvailable(
                requestDTO.getFitnessCertificateAvailable()
        );

        inspection.setPucAvailable(
                requestDTO.getPucAvailable()
        );

        inspection.setPermitAvailable(
                requestDTO.getPermitAvailable()
        );

        inspection.setDriverLicenseAvailable(
                requestDTO.getDriverLicenseAvailable()
        );

        inspection.setTaxDocumentValid(
                requestDTO.getTaxDocumentValid()
        );


        // 4. Physical Vehicle Inspection

        inspection.setBodyConditionOk(
                requestDTO.getBodyConditionOk()
        );

        inspection.setRustCorrosionOk(
                requestDTO.getRustCorrosionOk()
        );

        inspection.setDamageDentsOk(
                requestDTO.getDamageDentsOk()
        );

        inspection.setTyresOk(
                requestDTO.getTyresOk()
        );

        inspection.setLightsOk(
                requestDTO.getLightsOk()
        );

        inspection.setIndicatorsOk(
                requestDTO.getIndicatorsOk()
        );

        inspection.setMirrorsOk(
                requestDTO.getMirrorsOk()
        );

        inspection.setNumberPlateOk(
                requestDTO.getNumberPlateOk()
        );

        inspection.setWindshieldOk(
                requestDTO.getWindshieldOk()
        );

        inspection.setWipersOk(
                requestDTO.getWipersOk()
        );


        // Safety

        inspection.setBrakeOk(
                requestDTO.getBrakeOk()
        );

        inspection.setHandBrakeOk(
                requestDTO.getHandBrakeOk()
        );

        inspection.setReverseHornOk(
                requestDTO.getReverseHornOk()
        );

        inspection.setHornOk(
                requestDTO.getHornOk()
        );

        inspection.setFireExtinguisherAvailable(
                requestDTO.getFireExtinguisherAvailable()
        );

        inspection.setWheelChocksAvailable(
                requestDTO.getWheelChocksAvailable()
        );

        inspection.setFirstAidKitAvailable(
                requestDTO.getFirstAidKitAvailable()
        );

        inspection.setReflectiveTapeAvailable(
                requestDTO.getReflectiveTapeAvailable()
        );


        // 5. Container / Body Inspection

        inspection.setCleanlinessOk(
                requestDTO.getCleanlinessOk()
        );

        inspection.setPreviousLoad(
                requestDTO.getPreviousLoad()
        );

        inspection.setContaminationFree(
                requestDTO.getContaminationFree()
        );

        inspection.setLeakageFree(
                requestDTO.getLeakageFree()
        );

        inspection.setDamageFree(
                requestDTO.getDamageFree()
        );

        inspection.setSharpEdgesFree(
                requestDTO.getSharpEdgesFree()
        );

        inspection.setPestInsectsFree(
                requestDTO.getPestInsectsFree()
        );


        // 6. Vehicle Capacity Check

        Double vehicleCapacity =
                vehicle.getMaxWeightCapacity();

        Double shipmentWeight =
                shipment.getTotalWeight();

        inspection.setVehicleCapacity(
                vehicleCapacity
        );

        inspection.setShipmentWeight(
                shipmentWeight
        );

        if (vehicleCapacity == null ||
                shipmentWeight == null) {

            inspection.setCapacityResult(
                    "NOT_CHECKED"
            );

        } else if (shipmentWeight <= vehicleCapacity) {

            inspection.setCapacityResult(
                    "OK"
            );

        } else {

            inspection.setCapacityResult(
                    "REJECT / CHANGE VEHICLE"
            );
        }


        if ("REJECT / CHANGE VEHICLE".equals(
                inspection.getCapacityResult()
        )) {

            inspection.setInspectionResult(
                    InspectionResult.REJECTED
            );

        } else {

            inspection.setInspectionResult(
                    InspectionResult.PASSED
            );
        }


        // Admin Review

        inspection.setGuardVerificationStatus(GuardVerificationStatus.PENDING);

        inspection.setReviewStatus(
                InspectionReviewStatus.PENDING_REVIEW
        );

        inspection.setReviewRemarks(null);

        inspection.setReviewedAt(null);


        // Remarks

        inspection.setRemarks(
                requestDTO.getRemarks()
        );

        inspection.setInspectedAt(
                LocalDateTime.now()
        );

        inspection.setCreatedAt(
                LocalDateTime.now()
        );


        // FILES

        setRcFile(
                inspection,
                requestDTO.getRcFile()
        );

        setInsuranceFile(
                inspection,
                requestDTO.getInsuranceFile()
        );

        setFitnessCertificateFile(
                inspection,
                requestDTO.getFitnessCertificateFile()
        );

        setPucFile(
                inspection,
                requestDTO.getPucFile()
        );

        setPermitFile(
                inspection,
                requestDTO.getPermitFile()
        );

        setTaxDocumentFile(
                inspection,
                requestDTO.getTaxDocumentFile()
        );

        setDrivingLicenseFile(
                inspection,
                requestDTO.getDrivingLicenseFile()
        );

        setDriverIdGatePassFile(
                inspection,
                requestDTO.getDriverIdGatePassFile()
        );


        VehicleInspection savedInspection =
                vehicleInspectionRepository.save(
                        inspection
                );

        ShipmentStatus currentStatus = shipment.getStatus();

        shipment.setStatus(
                ShipmentStatus.WAITING_FOR_VEHICLE_APPROVAL
        );

        shipmentRepository.save(shipment);

        return mapToResponse(savedInspection);

    }


    @Override
    @Transactional(readOnly = true)
    public List<VehicleInspectionResponseDTO> getGuardPendingInspections() {
        return vehicleInspectionRepository
                .findByGuardVerificationStatusIn(List.of(
                        GuardVerificationStatus.PENDING,
                        GuardVerificationStatus.ON_HOLD
                ))
                .stream()
                .map(inspection -> {
                    VehicleInspectionResponseDTO response =
                            mapToResponse(inspection);

                    Trip trip = inspection.getTrip();

                    TransporterAppointmentLetter letter =
                            transporterAppointmentLetterRepository
                                    .findByTrip_Id(trip.getId())
                                    .orElse(null);

                    response.setAppointmentLetterStatus(
                            letter != null ? letter.getStatus() : null
                    );

                    return response;
                })
                .toList();
    }

    private void setRcFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setRcFile(
                    file.getBytes()
            );

            inspection.setRcFileName(
                    file.getOriginalFilename()
            );

            inspection.setRcContentType(
                    file.getContentType()
            );
        }
    }


    private void setInsuranceFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setInsuranceFile(
                    file.getBytes()
            );

            inspection.setInsuranceFileName(
                    file.getOriginalFilename()
            );

            inspection.setInsuranceContentType(
                    file.getContentType()
            );
        }
    }


    private void setFitnessCertificateFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setFitnessCertificateFile(
                    file.getBytes()
            );

            inspection.setFitnessCertificateFileName(
                    file.getOriginalFilename()
            );

            inspection.setFitnessCertificateContentType(
                    file.getContentType()
            );
        }
    }


    private void setPucFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setPucFile(
                    file.getBytes()
            );

            inspection.setPucFileName(
                    file.getOriginalFilename()
            );

            inspection.setPucContentType(
                    file.getContentType()
            );
        }
    }


    private void setPermitFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setPermitFile(
                    file.getBytes()
            );

            inspection.setPermitFileName(
                    file.getOriginalFilename()
            );

            inspection.setPermitContentType(
                    file.getContentType()
            );
        }
    }


    private void setTaxDocumentFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setTaxDocumentFile(
                    file.getBytes()
            );

            inspection.setTaxDocumentFileName(
                    file.getOriginalFilename()
            );

            inspection.setTaxDocumentContentType(
                    file.getContentType()
            );
        }
    }


    private void setDrivingLicenseFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setDrivingLicenseFile(
                    file.getBytes()
            );

            inspection.setDrivingLicenseFileName(
                    file.getOriginalFilename()
            );

            inspection.setDrivingLicenseContentType(
                    file.getContentType()
            );
        }
    }


    private void setDriverIdGatePassFile(
            VehicleInspection inspection,
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        if (file != null && !file.isEmpty()) {

            inspection.setDriverIdGatePassFile(
                    file.getBytes()
            );

            inspection.setDriverIdGatePassFileName(
                    file.getOriginalFilename()
            );

            inspection.setDriverIdGatePassContentType(
                    file.getContentType()
            );
        }
    }


    private void validateFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            return;
        }

        String contentType =
                file.getContentType();

        if (!"application/pdf".equalsIgnoreCase(contentType)
                && !"image/jpeg".equalsIgnoreCase(contentType)
                && !"image/jpg".equalsIgnoreCase(contentType)
                && !"image/png".equalsIgnoreCase(contentType)) {

            throw new RuntimeException(
                    "Only PDF, JPG, JPEG and PNG files are allowed"
            );
        }
    }


    @Override
    public VehicleInspectionResponseDTO getInspectionById(
            Long id
    ) {

        VehicleInspection inspection =
                vehicleInspectionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle inspection not found: " + id
                                )
                        );

        return mapToResponse(inspection);
    }


    @Override
    @Transactional(readOnly = true)
    public List<VehicleInspectionResponseDTO> getAllInspections() {

        return vehicleInspectionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<VehicleInspectionResponseDTO> getPendingReviews() {

        return vehicleInspectionRepository
                .findByReviewStatus(
                        InspectionReviewStatus.PENDING_REVIEW
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public VehicleInspectionResponseDTO reviewInspection(
            Long id,
            VehicleInspectionReviewRequestDTO requestDTO
    ) {

        if (requestDTO.getStatus() == null) {
            throw new RuntimeException(
                    "Review status is required"
            );
        }

        VehicleInspection inspection =
                vehicleInspectionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle inspection not found: " + id
                                )
                        );

        if (inspection.getReviewStatus() !=
                InspectionReviewStatus.PENDING_REVIEW) {

            throw new RuntimeException(
                    "Vehicle inspection has already been reviewed"
            );
        }

        if (requestDTO.getStatus() !=
                InspectionReviewStatus.APPROVED
                &&
                requestDTO.getStatus() !=
                        InspectionReviewStatus.REJECTED) {

            throw new RuntimeException(
                    "Only APPROVED or REJECTED status is allowed"
            );
        }

        inspection.setReviewStatus(
                requestDTO.getStatus()
        );

        inspection.setReviewRemarks(
                requestDTO.getRemarks()
        );

        inspection.setReviewedAt(
                LocalDateTime.now()
        );

        Shipment shipment = inspection.getTrip().getShipment();

        if (shipment == null) {
            throw new RuntimeException("Shipment not found for inspection");
        }

        if (requestDTO.getStatus() == InspectionReviewStatus.APPROVED) {
            shipment.setStatus(
                    ShipmentStatus.WAITING_FOR_APPOINTMENT_LETTER
            );
        } else if (requestDTO.getStatus() == InspectionReviewStatus.REJECTED) {
            shipment.setStatus(ShipmentStatus.VEHICLE_REJECTED);
        }

        shipmentRepository.save(shipment);


        VehicleInspection savedInspection =
                vehicleInspectionRepository.save(
                        inspection
                );

        return mapToResponse(savedInspection);
    }



    @Override
    @Transactional(readOnly = true)
    public List<VehicleInspectionAdminResponseDTO> getAdminInspections(
            String status
    ) {

        List<VehicleInspection> inspections;

        if (status == null || status.equalsIgnoreCase("ALL")) {

            inspections = vehicleInspectionRepository.findAll();

        } else {

            InspectionReviewStatus reviewStatus;

            if (status.equalsIgnoreCase("PENDING")) {
                reviewStatus = InspectionReviewStatus.PENDING_REVIEW;
            } else {
                try {
                    reviewStatus = InspectionReviewStatus.valueOf(
                            status.toUpperCase()
                    );
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "Invalid status. Use ALL, PENDING, APPROVED or REJECTED"
                    );
                }
            }

            inspections = vehicleInspectionRepository.findByReviewStatus(
                    reviewStatus
            );
        }

        return inspections.stream()
                .map(this::mapToAdminResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public VehicleInspectionAdminResponseDTO getAdminInspectionById(
            Long id
    ) {

        VehicleInspection inspection =
                vehicleInspectionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle inspection not found: " + id
                                )
                        );

        return mapToAdminResponse(inspection);
    }


    private VehicleInspectionAdminResponseDTO mapToAdminResponse(
            VehicleInspection inspection
    ) {

        VehicleInspectionAdminResponseDTO response =
                new VehicleInspectionAdminResponseDTO();

        Trip trip = inspection.getTrip();

        if (trip == null) {
            throw new RuntimeException(
                    "Trip is not associated with inspection: " +
                            inspection.getId()
            );
        }

        Vehicle vehicle = trip.getVehicle();
        Shipment shipment = trip.getShipment();

        response.setVehicleInspection(
                mapToResponse(inspection)
        );

        TransporterAppointmentLetter appointmentLetter =
                transporterAppointmentLetterRepository
                        .findByTrip_Id(trip.getId())
                        .orElse(null);

        response.setAppointmentLetterStatus(
                appointmentLetter != null
                        ? appointmentLetter.getStatus()
                        : null
        );

        if (shipment != null) {
            response.setShipment(
                    mapToShipmentResponse(shipment)
            );
        }

        if (vehicle != null && vehicle.getTransporter() != null) {

            response.setTransporter(
                    mapToTransporterResponse(
                            vehicle.getTransporter()
                    )
            );
        }

        if (shipment != null) {

            List<SalesOrderResponseDTO> salesOrders =
                    shipmentOrderRepository.findByShipmentId(shipment.getId())
                            .stream()
                            .map(shipmentOrder ->
                                    jdeSalesOrderRepository
                                            .findById(shipmentOrder.getOrderId())
                                            .orElse(null)
                            )
                            .filter(Objects::nonNull)
                            .map(this::mapToSalesOrderResponse)
                            .toList();

            response.setSalesOrders(salesOrders);

        } else {

            response.setSalesOrders(List.of());
        }

        return response;
    }


    private VehicleInspectionShipmentResponseDTO mapToShipmentResponse(
            Shipment shipment
    ) {

        VehicleInspectionShipmentResponseDTO response =
                new VehicleInspectionShipmentResponseDTO();

        response.setId(shipment.getId());
        response.setShipmentNo(shipment.getShipmentNo());
        response.setRouteId(shipment.getRouteId());
        response.setPlannedDate(shipment.getPlannedDate());
        response.setTotalWeight(shipment.getTotalWeight());
        response.setTotalVolume(shipment.getTotalVolume());
        response.setSelectedVehicleType(shipment.getSelectedVehicleType());
        response.setSelectedVehicleCount(shipment.getSelectedVehicleCount());
        response.setStatus(shipment.getStatus());

        if (shipment.getVehicle() != null) {
            response.setVehicleId(
                    shipment.getVehicle().getId()
            );
        }

        response.setExpectedPrice(shipment.getExpectedPrice());
        response.setPlannedPickupDate(shipment.getPlannedPickupDate());
        response.setBidMessage(shipment.getBidMessage());

        return response;
    }


    private VehicleInspectionTransporterResponseDTO mapToTransporterResponse(
            Transporter transporter
    ) {

        return VehicleInspectionTransporterResponseDTO.builder()
                .id(transporter.getId())
                .companyName(transporter.getCompanyName())
                .gstNumber(transporter.getGstNumber())
                .panNumber(transporter.getPanNumber())
                .contactPerson(transporter.getContactPerson())
                .mobile(transporter.getMobile())
                .email(transporter.getEmail())
                .address(transporter.getAddress())
                .city(transporter.getCity())
                .state(transporter.getState())
                .pincode(transporter.getPincode())
                .status(transporter.getStatus())
                .addressNumber(transporter.getAddressNumber())
                .build();
    }


    private SalesOrderResponseDTO mapToSalesOrderResponse(
            JdeSalesOrder order
    ) {

        SalesOrderResponseDTO response =
                new SalesOrderResponseDTO();

        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setOrderType(order.getOrderType());
        response.setCompany(order.getCompany());
        response.setCustomerNumber(order.getCustomerNumber());
        response.setOrderDate(order.getOrderDate());
        response.setRequestedDate(order.getRequestedDate());
        response.setBranchPlant(order.getBranchPlant());
        response.setStatus(order.getStatus());
        response.setCurrencyCode(order.getCurrencyCode());
        response.setShipToCustomerNumber(order.getShipToCustomerNumber());
        response.setShipToCustomerName(order.getShipToCustomerName());
        response.setShipToAddress1(order.getShipToAddress1());
        response.setShipToCity(order.getShipToCity());
        response.setShipToState(order.getShipToState());
        response.setShipToPinCode(order.getShipToPinCode());
        response.setTotalWeight(order.getTotalWeight());
        response.setOrderStatus(order.getOrderStatus());
        response.setWeightUnit(order.getWeightUnit());
        response.setTotalVolume(order.getTotalVolume());
        response.setVolumeUnit(order.getVolumeUnit());

        if (order.getLines() != null) {

            response.setLines(
                    order.getLines()
                            .stream()
                            .map(this::mapToSalesOrderLineResponse)
                            .toList()
            );

        } else {

            response.setLines(List.of());
        }

        return response;
    }


    private SalesOrderLineResponseDTO mapToSalesOrderLineResponse(
            JdeSalesOrderLine line
    ) {

        SalesOrderLineResponseDTO response =
                new SalesOrderLineResponseDTO();

        response.setId(line.getId());
        response.setLineNumber(line.getLineNumber());
        response.setItemNumber(line.getItemNumber());
        response.setDescription(line.getDescription());
        response.setQuantity(line.getQuantity());
        response.setUom(line.getUom());
        response.setUnitPrice(line.getUnitPrice());
        response.setLineAmount(line.getLineAmount());

        return response;
    }


    private VehicleInspectionResponseDTO mapToResponse(
            VehicleInspection inspection
    ) {

        VehicleInspectionResponseDTO response =
                new VehicleInspectionResponseDTO();

        Trip trip = inspection.getTrip();

        Vehicle vehicle = trip.getVehicle();

        Shipment shipment = trip.getShipment();


        response.setInspectionId(
                inspection.getId()
        );

        response.setTripId(
                trip.getId()
        );

        response.setTripNo(
                trip.getTripNo()
        );

        response.setShipmentId(
                shipment.getId()
        );

        response.setShipmentNo(
                shipment.getShipmentNo()
        );


        response.setVehicleId(
                vehicle.getId()
        );

        response.setVehicleNumber(
                vehicle.getVehicleNumber()
        );

        response.setVehicleType(
                vehicle.getVehicleType()
        );

        response.setVehicleCapacity(
                inspection.getVehicleCapacity()
        );

        response.setWeightUnit(
                vehicle.getWeightUnit()
        );


        response.setDriverName(
                trip.getDriverName()
        );

        response.setDriverMobile(
                trip.getDriverMobile()
        );


        // Identification

        response.setCarrierCode(
                inspection.getCarrierCode()
        );

        response.setArrivalDate(
                inspection.getArrivalDate()
        );

        response.setArrivalTime(
                inspection.getArrivalTime()
        );

        response.setGateEntryNo(
                inspection.getGateEntryNo()
        );


        // Driver Inspection

        response.setDrivingLicenseNumber(
                inspection.getDrivingLicenseNumber()
        );

        response.setLicenseValidity(
                inspection.getLicenseValidity()
        );

        response.setDriverIdGatePass(
                inspection.getDriverIdGatePass()
        );


        // Vehicle Documents

        response.setRcAvailable(
                inspection.getRcAvailable()
        );

        response.setInsuranceAvailable(
                inspection.getInsuranceAvailable()
        );

        response.setFitnessCertificateAvailable(
                inspection.getFitnessCertificateAvailable()
        );

        response.setPucAvailable(
                inspection.getPucAvailable()
        );

        response.setPermitAvailable(
                inspection.getPermitAvailable()
        );

        response.setDriverLicenseAvailable(
                inspection.getDriverLicenseAvailable()
        );

        response.setTaxDocumentValid(
                inspection.getTaxDocumentValid()
        );


        // Physical Inspection

        response.setBodyConditionOk(
                inspection.getBodyConditionOk()
        );

        response.setRustCorrosionOk(
                inspection.getRustCorrosionOk()
        );

        response.setDamageDentsOk(
                inspection.getDamageDentsOk()
        );

        response.setTyresOk(
                inspection.getTyresOk()
        );

        response.setLightsOk(
                inspection.getLightsOk()
        );

        response.setIndicatorsOk(
                inspection.getIndicatorsOk()
        );

        response.setMirrorsOk(
                inspection.getMirrorsOk()
        );

        response.setNumberPlateOk(
                inspection.getNumberPlateOk()
        );

        response.setWindshieldOk(
                inspection.getWindshieldOk()
        );

        response.setWipersOk(
                inspection.getWipersOk()
        );


        // Safety

        response.setBrakeOk(
                inspection.getBrakeOk()
        );

        response.setHandBrakeOk(
                inspection.getHandBrakeOk()
        );

        response.setReverseHornOk(
                inspection.getReverseHornOk()
        );

        response.setHornOk(
                inspection.getHornOk()
        );

        response.setFireExtinguisherAvailable(
                inspection.getFireExtinguisherAvailable()
        );

        response.setWheelChocksAvailable(
                inspection.getWheelChocksAvailable()
        );

        response.setFirstAidKitAvailable(
                inspection.getFirstAidKitAvailable()
        );

        response.setReflectiveTapeAvailable(
                inspection.getReflectiveTapeAvailable()
        );


        // Container

        response.setCleanlinessOk(
                inspection.getCleanlinessOk()
        );

        response.setPreviousLoad(
                inspection.getPreviousLoad()
        );

        response.setContaminationFree(
                inspection.getContaminationFree()
        );

        response.setLeakageFree(
                inspection.getLeakageFree()
        );

        response.setDamageFree(
                inspection.getDamageFree()
        );

        response.setSharpEdgesFree(
                inspection.getSharpEdgesFree()
        );

        response.setPestInsectsFree(
                inspection.getPestInsectsFree()
        );


        // Capacity

        response.setShipmentWeight(
                inspection.getShipmentWeight()
        );

        response.setCapacityResult(
                inspection.getCapacityResult()
        );


        // Technical Inspection Result

        response.setInspectionResult(
                inspection.getInspectionResult()
        );


        // Guard Verification

        response.setGuardVerificationStatus(inspection.getGuardVerificationStatus());
        response.setGuardVerificationRemarks(inspection.getGuardVerificationRemarks());
        response.setGuardVerifiedAt(inspection.getGuardVerifiedAt());

        gatePassRepository.findByVehicleInspectionId(inspection.getId())
                .ifPresent(gatePass -> {
                    response.setGateId(gatePass.getGateId());
                    response.setGateInAt(gatePass.getGateInAt());
                    response.setGateOutAt(gatePass.getGateOutAt());
                });

        // Appointment Letter Details
        transporterAppointmentLetterRepository
                .findByTrip_Id(trip.getId())
                .ifPresent(letter -> {
                    response.setAppointmentLetterId(letter.getId());
                    response.setAppointmentNumber(letter.getAppointmentNumber());
                    response.setAppointmentLetterStatus(letter.getStatus());
                    response.setTransporterAcceptance(letter.getTransporterAcceptance());
                    response.setTransporterRemarks(letter.getTransporterRemarks());
                    response.setTransporterSignatureUrl(letter.getTransporterSignatureUrl());

                    response.setReportingGate(letter.getReportingGate());
                    response.setLoadingBay(letter.getLoadingBay());
                    response.setPickupLocation(letter.getPickupLocation());

                    response.setReportingDate(letter.getReportingDate());
                    response.setReportingTime(letter.getReportingTime());
                    response.setLoadingWindow(letter.getLoadingWindow());

                    response.setExpectedArrivalDate(letter.getExpectedArrivalDate());
                    response.setExpectedArrivalTime(letter.getExpectedArrivalTime());
                });

        // Admin Review

        response.setReviewStatus(
                inspection.getReviewStatus()
        );

        response.setReviewRemarks(
                inspection.getReviewRemarks()
        );

        response.setReviewedAt(
                inspection.getReviewedAt()
        );


        // General Remarks

        response.setRemarks(
                inspection.getRemarks()
        );

        response.setInspectedAt(
                inspection.getInspectedAt()
        );


        // FILES

        if (inspection.getRcFile() != null) {

            response.setRcFileName(
                    inspection.getRcFileName()
            );

            response.setRcFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/rc"
            );
        }


        if (inspection.getInsuranceFile() != null) {

            response.setInsuranceFileName(
                    inspection.getInsuranceFileName()
            );

            response.setInsuranceFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/insurance"
            );
        }


        if (inspection.getFitnessCertificateFile() != null) {

            response.setFitnessCertificateFileName(
                    inspection.getFitnessCertificateFileName()
            );

            response.setFitnessCertificateFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/fitness"
            );
        }


        if (inspection.getPucFile() != null) {

            response.setPucFileName(
                    inspection.getPucFileName()
            );

            response.setPucFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/puc"
            );
        }


        if (inspection.getPermitFile() != null) {

            response.setPermitFileName(
                    inspection.getPermitFileName()
            );

            response.setPermitFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/permit"
            );
        }


        if (inspection.getTaxDocumentFile() != null) {

            response.setTaxDocumentFileName(
                    inspection.getTaxDocumentFileName()
            );

            response.setTaxDocumentFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/tax"
            );
        }


        if (inspection.getDrivingLicenseFile() != null) {

            response.setDrivingLicenseFileName(
                    inspection.getDrivingLicenseFileName()
            );

            response.setDrivingLicenseFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/driving-license"
            );
        }


        if (inspection.getDriverIdGatePassFile() != null) {

            response.setDriverIdGatePassFileName(
                    inspection.getDriverIdGatePassFileName()
            );

            response.setDriverIdGatePassFileUrl(
                    "/api/vehicle-inspections/" +
                            inspection.getId() +
                            "/files/driver-id"
            );
        }


        return response;
    }
}
